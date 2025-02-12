#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h> //socket.h includes a number of definitions of structures needed for sockets.
#include <netinet/in.h> //in.h contains constants and structures needed for internet domain addresses.
#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/tcp.h>
#include <ctime>
#include <sys/times.h>


#include <stdarg.h>

#include <assert.h>
#include <ctype.h>
#include <unistd.h>


#include <errno.h>
#include "rtmp_client.h"


#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define RTMP_PACKET_TYPE_BYTES_READ_REPORT  0x03

#define GetSockError() errno
#define OBS_OUTPUT_BAD_PATH -1
#define RTMP_DEFAULT_CHUNKSIZE	128
#define TRUE	1
#define FALSE	0

#define RTMP_MAX_HEADER_SIZE 18
#define RTMP_PACKET_SIZE_LARGE    0
#define RTMP_PACKET_SIZE_MEDIUM   1
#define RTMP_PACKET_SIZE_SMALL    2
#define RTMP_PACKET_SIZE_MINIMUM  3
#define RTMP_PACKET_TYPE_INVOKE             0x14

#define RTMP_SIG_SIZE 1536
#define RTMP_LARGE_HEADER_SIZE 12

static const int packetSize[] = { 12, 8, 4, 1 };
#ifndef _WIN32
static int clk_tck;
#endif
#define SAVC(x)	static const AVal av_##x = AVC(#x)
SAVC(FCUnpublish);


typedef enum {
    RTMPT_OPEN=0, RTMPT_SEND, RTMPT_IDLE, RTMPT_CLOSE
} RTMPTCmd;
int RTMP_ctrlC;

const char RTMPProtocolStrings[][7] = {
        "RTMP",
        "RTMPT",
        "RTMPE",
        "RTMPTE",
        "RTMPS",
        "RTMPTS",
        "",
        "",
        "RTMFP"
};



void AMF_AddProp(AMFObject *obj, const AMFObjectProperty *prop){
    if (!(obj->o_num & 0x0f))
        obj->o_props =
                static_cast<AMFObjectProperty *>(realloc(obj->o_props, (obj->o_num + 16) *
                                                                       sizeof(AMFObjectProperty)));
    memcpy(&obj->o_props[obj->o_num++], prop, sizeof(AMFObjectProperty));
}

RTMP *RTMP_Alloc(){
    return static_cast<RTMP *>(calloc(1, sizeof(RTMP)));
}
static const char *RTMPT_cmds[] = {
        "open",
        "send",
        "idle",
        "close"
};
void RTMP_Init(RTMP *r){
#ifdef CRYPTO
    if (!RTMP_TLS_ctx)
    RTMP_TLS_Init();
#endif

    memset(r, 0, sizeof(RTMP));
    r->m_sb.sb_socket = -1;
    r->m_inChunkSize = RTMP_DEFAULT_CHUNKSIZE;
    r->m_outChunkSize = RTMP_DEFAULT_CHUNKSIZE;
    r->m_nBufferMS = 30000;
    r->m_nClientBW = 2500000;
    r->m_nClientBW2 = 2;
    r->m_nServerBW = 2500000;
    r->m_fAudioCodecs = 3191.0;
    r->m_fVideoCodecs = 252.0;
    r->Link.receiveTimeoutInMs = 10000;
    r->Link.swfAge = 30;
}
static const char *optinfo[] = {
        "string", "integer", "boolean", "AMF" };

static int parseAMF(AMFObject *obj, AVal *av, int *depth)
{
    AMFObjectProperty prop = {{0,0}};
    int i;
    char *p, *arg = av->av_val;

    if (arg[1] == ':')
    {
        p = (char *)arg+2;
        switch(arg[0])
        {
            case 'B':
                prop.p_type = AMF_BOOLEAN;
                prop.p_vu.p_number = atoi(p);
                break;
            case 'S':
                prop.p_type = AMF_STRING;
                prop.p_vu.p_aval.av_val = p;
                prop.p_vu.p_aval.av_len = av->av_len - (p-arg);
                break;
            case 'N':
                prop.p_type = AMF_NUMBER;
                prop.p_vu.p_number = strtod(p, NULL);
                break;
            case 'Z':
                prop.p_type = AMF_NULL;
                break;
            case 'O':
                i = atoi(p);
                if (i)
                {
                    prop.p_type = AMF_OBJECT;
                }
                else
                {
                    (*depth)--;
                    return 0;
                }
                break;
            default:
                return -1;
        }
    }
    else if (arg[2] == ':' && arg[0] == 'N')
    {
        p = strchr(arg+3, ':');
        if (!p || !*depth)
            return -1;
        prop.p_name.av_val = (char *)arg+3;
        prop.p_name.av_len = p - (arg+3);

        p++;
        switch(arg[1])
        {
            case 'B':
                prop.p_type = AMF_BOOLEAN;
                prop.p_vu.p_number = atoi(p);
                break;
            case 'S':
                prop.p_type = AMF_STRING;
                prop.p_vu.p_aval.av_val = p;
                prop.p_vu.p_aval.av_len = av->av_len - (p-arg);
                break;
            case 'N':
                prop.p_type = AMF_NUMBER;
                prop.p_vu.p_number = strtod(p, NULL);
                break;
            case 'O':
                prop.p_type = AMF_OBJECT;
                break;
            default:
                return -1;
        }
    }
    else
        return -1;

    if (*depth)
    {
        AMFObject *o2;
        for (i=0; i<*depth; i++)
        {
            o2 = &obj->o_props[obj->o_num-1].p_vu.p_object;
            obj = o2;
        }
    }
    AMF_AddProp(obj, &prop);
    if (prop.p_type == AMF_OBJECT)
        (*depth)++;
    return 0;
}
static void RTMP_OptUsage(){
    int i;

    LOGI("RTMP_OptUsage",  "Valid RTMP options are:");
    for (i=0; options[i].name.av_len; i++) {

        LOGI("RTMP_OptUsage",  "%10s %-7s  %s\n", options[i].name.av_val,
             optinfo[options[i].otype], options[i].use);
    }
}
int RTMP_SetOpt(RTMP *r, const AVal *opt, AVal *arg){
    int i;
    void *v;

    for (i=0; options[i].name.av_len; i++) {
        if (opt->av_len != options[i].name.av_len) continue;
        if (strcasecmp(opt->av_val, options[i].name.av_val)) continue;
        v = (char *)r + options[i].off;
        switch(options[i].otype) {
            case OPT_STR: {
                AVal *aptr = static_cast<AVal *>(v);
                *aptr = *arg; }
                break;
            case OPT_INT: {
                long l = strtol(arg->av_val, NULL, 0);
                *(int *)v = l; }
                break;
            case OPT_BOOL: {
                int j, fl;
                fl = *(int *)v;
                for (j=0; truth[j].av_len; j++) {
                    if (arg->av_len != truth[j].av_len) continue;
                    if (strcasecmp(arg->av_val, truth[j].av_val)) continue;
                    fl |= options[i].omisc; break; }
                *(int *)v = fl;
            }
                break;
            case OPT_CONN:
                if (parseAMF(&r->Link.extras, arg, &r->Link.edepth)) {
                    return RTMP_ERROR_UNKNOWN_RTMP_AMF_TYPE;
                }
                break;
        }
        break;
    }
    if (!options[i].name.av_len) {

        LOGI("RTMP_SetOpt",  "Unknown option %s", opt->av_val);
        RTMP_OptUsage();
        return RTMP_ERROR_UNKNOWN_RTMP_OPTION;
    }

    return RTMP_SUCCESS;
}
const char RTMPProtocolStringsLower[][7] = {
        "rtmp",
        "rtmpt",
        "rtmpe",
        "rtmpte",
        "rtmps",
        "rtmpts",
        "",
        "",
        "rtmfp"
};


static void SocksSetup(RTMP *r, AVal *sockshost){
    LOGI("SocksSetup",  "SOCKETS -->%d",sockshost->av_len);
    if (sockshost->av_len)
    {
        const char *socksport = strchr(sockshost->av_val, ':');
        char *hostname = strdup(sockshost->av_val);

        if (socksport)
            hostname[socksport - sockshost->av_val] = '\0';
        r->Link.sockshost.av_val = hostname;
        r->Link.sockshost.av_len = strlen(hostname);

        r->Link.socksport = socksport ? atoi(socksport + 1) : 1080;

        LOGI("SocksSetup",  "Connecting via SOCKS proxy: %s:%d", r->Link.sockshost.av_val,
             r->Link.socksport);
    }
    else
    {
        r->Link.sockshost.av_val = NULL;
        r->Link.sockshost.av_len = 0;
        r->Link.socksport = 0;
    }
}






RTMPResult RTMP_SetupURL(RTMP *r, char *url)
{
    AVal opt, arg;
    char *p1, *p2, *ptr = strchr(url, ' ');
    int ret, len;
    unsigned int port = 0;

    if (ptr)
        *ptr = '\0';

    len = strlen(url);
    ret = RTMP_ParseURL(url, &r->Link.protocol, &r->Link.hostname,
                        &port, &r->Link.playpath0, &r->Link.app);
    if (ret != RTMP_SUCCESS)
    {
        return static_cast<RTMPResult>(ret);
    }
    r->Link.port = port;
    r->Link.playpath = r->Link.playpath0;

    while (ptr) {
        *ptr++ = '\0';
        p1 = ptr;
        p2 = strchr(p1, '=');
        if (!p2)
            break;
        opt.av_val = p1;
        opt.av_len = p2 - p1;
        *p2++ = '\0';
        arg.av_val = p2;
        ptr = strchr(p2, ' ');
        if (ptr) {
            *ptr = '\0';
            arg.av_len = ptr - p2;
            /* skip repeated spaces */
            while(ptr[1] == ' ')
                *ptr++ = '\0';
        } else {
            arg.av_len = strlen(p2);
        }

        /* unescape */
        port = arg.av_len;
        for (p1=p2; port >0;) {
            if (*p1 == '\\') {
                unsigned int c;
                if (port < 3)
                {
                    return RTMP_ERROR_URL_INCORRECT_PORT;
                }
                sscanf(p1+1, "%02x", &c);
                *p2++ = c;
                port -= 3;
                p1 += 3;
            } else {
                *p2++ = *p1++;
                port--;
            }
        }
        arg.av_len = p2 - arg.av_val;

        ret = RTMP_SetOpt(r, &opt, &arg);
        if (ret != RTMP_SUCCESS)
            return static_cast<RTMPResult>(ret);
    }

    if (!r->Link.tcUrl.av_len)
    {
        r->Link.tcUrl.av_val = url;
        if (r->Link.app.av_len)
        {
            if (r->Link.app.av_val < url + len)
            {
                /* if app is part of original url, just use it */
                r->Link.tcUrl.av_len = r->Link.app.av_len + (r->Link.app.av_val - url);
            }
            else
            {
                len = r->Link.hostname.av_len + r->Link.app.av_len +
                      sizeof("rtmpte://:65535/");
                r->Link.tcUrl.av_val = static_cast<char *>(malloc(len));
                r->Link.tcUrl.av_len = snprintf(r->Link.tcUrl.av_val, len,
                                                "%s://%.*s:%d/%.*s",
                                                RTMPProtocolStringsLower[r->Link.protocol],
                                                r->Link.hostname.av_len, r->Link.hostname.av_val,
                                                r->Link.port,
                                                r->Link.app.av_len, r->Link.app.av_val);
                r->Link.lFlags |= RTMP_LF_FTCU;
            }
        }
        else
        {
            r->Link.tcUrl.av_len = strlen(url);
        }
    }

#ifdef CRYPTO
    if ((r->Link.lFlags & RTMP_LF_SWFV) && r->Link.swfUrl.av_len)
    RTMP_HashSWF(r->Link.swfUrl.av_val, &r->Link.SWFSize,
	  (unsigned char *)r->Link.SWFHash, r->Link.swfAge);
#endif

    SocksSetup(r, &r->Link.sockshost);

    if (r->Link.port == 0)
    {
        if (r->Link.protocol & RTMP_FEATURE_SSL)
            r->Link.port = 443;
        else if (r->Link.protocol & RTMP_FEATURE_HTTP)
            r->Link.port = 80;
        else
            r->Link.port = 1935;
    }
    return RTMP_SUCCESS;
}
void RTMP_EnableWrite(RTMP *r)
{
    r->Link.protocol |= RTMP_FEATURE_WRITE;
}
static RTMPResult
add_addr_info(struct sockaddr_in *service, AVal *host, int port)
{
    char *hostname;
    RTMPResult ret = RTMP_SUCCESS;
    if (host->av_val[host->av_len])
    {
        hostname = static_cast<char *>(malloc(host->av_len + 1));
        memcpy(hostname, host->av_val, host->av_len);
        hostname[host->av_len] = '\0';
    }
    else
    {
        hostname = host->av_val;
    }

    service->sin_addr.s_addr = inet_addr(hostname);
    if (service->sin_addr.s_addr == INADDR_NONE)
    {
        struct hostent *host = gethostbyname(hostname);
        if (host == NULL || host->h_addr == NULL)
        {

            LOGI("RTMP_ParseURL",  "Problem accessing the DNS. (addr: %s)", hostname);
            ret = RTMP_ERROR_DNS_NOT_REACHABLE;
            goto finish;
        }
        service->sin_addr = *(struct in_addr *)host->h_addr;
    }

    service->sin_port = htons(port);
    finish:
    if (hostname != host->av_val)
        free(hostname);
    return ret;
}
int RTMP_IsConnected(RTMP *r){
    return r->m_sb.sb_socket != -1;
}



static int EncodeInt32LE(char *output, int nVal){
    output[0] = nVal;
    nVal >>= 8;
    output[1] = nVal;
    nVal >>= 8;
    output[2] = nVal;
    nVal >>= 8;
    output[3] = nVal;
    return 4;
}
/* Data is Big-Endian */
unsigned short AMF_DecodeInt16(const char *data){
    unsigned char *c = (unsigned char *) data;
    unsigned short val;
    val = (c[0] << 8) | c[1];
    return val;
}

void AMF_DecodeString(const char *data, AVal *bv){
    bv->av_len = AMF_DecodeInt16(data);
    bv->av_val = (bv->av_len > 0) ? (char *)data + 2 : NULL;
}


double AMF_DecodeNumber(const char *data){
    double dVal;
#if __FLOAT_WORD_ORDER == __BYTE_ORDER
    #if __BYTE_ORDER == __BIG_ENDIAN
  memcpy(&dVal, data, 8);
#elif __BYTE_ORDER == __LITTLE_ENDIAN
  unsigned char *ci, *co;
  ci = (unsigned char *)data;
  co = (unsigned char *)&dVal;
  co[0] = ci[7];
  co[1] = ci[6];
  co[2] = ci[5];
  co[3] = ci[4];
  co[4] = ci[3];
  co[5] = ci[2];
  co[6] = ci[1];
  co[7] = ci[0];
#endif
#else
#if __BYTE_ORDER == __LITTLE_ENDIAN	/* __FLOAT_WORD_ORER == __BIG_ENDIAN */
    unsigned char *ci, *co;
    ci = (unsigned char *)data;
    co = (unsigned char *)&dVal;
    co[0] = ci[3];
    co[1] = ci[2];
    co[2] = ci[1];
    co[3] = ci[0];
    co[4] = ci[7];
    co[5] = ci[6];
    co[6] = ci[5];
    co[7] = ci[4];
#else /* __BYTE_ORDER == __BIG_ENDIAN && __FLOAT_WORD_ORER == __LITTLE_ENDIAN */
    unsigned char *ci, *co;
  ci = (unsigned char *)data;
  co = (unsigned char *)&dVal;
  co[0] = ci[4];
  co[1] = ci[5];
  co[2] = ci[6];
  co[3] = ci[7];
  co[4] = ci[0];
  co[5] = ci[1];
  co[6] = ci[2];
  co[7] = ci[3];
#endif
#endif
    return dVal;
}

static void AV_queue(RTMP_METHOD **vals, int *num, AVal *av, int txn)
{
    char *tmp;
    if (!(*num & 0x0f))
        *vals = static_cast<RTMP_METHOD *>(realloc(*vals, (*num + 16) * sizeof(RTMP_METHOD)));
    tmp = static_cast<char *>(malloc(av->av_len + 1));
    memcpy(tmp, av->av_val, av->av_len);
    tmp[av->av_len] = '\0';
    (*vals)[*num].num = txn;
    (*vals)[*num].name.av_len = av->av_len;
    (*vals)[(*num)++].name.av_val = tmp;
}



RTMPResult RTMP_SendPacket(RTMP *r, RTMPPacket *packet, int queue){
    const RTMPPacket *prevPacket;
    uint32_t last = 0;
    int nSize;
    int hSize, cSize;
    char *header, *hptr, *hend, hbuf[RTMP_MAX_HEADER_SIZE], c;
    uint32_t t;
    char *buffer, *tbuf = NULL, *toff = NULL;
    int nChunkSize;
    int tlen;

    if (packet->m_nChannel >= r->m_channelsAllocatedOut)
    {
        int n = packet->m_nChannel + 10;
        RTMPPacket **packets = static_cast<RTMPPacket **>(realloc(r->m_vecChannelsOut,
                                                                  sizeof(RTMPPacket *) * n));
        if (!packets) {
            free(r->m_vecChannelsOut);
            r->m_vecChannelsOut = NULL;
            r->m_channelsAllocatedOut = 0;
            return RTMP_ERROR_MEM_ALLOC_FAIL;
        }
        r->m_vecChannelsOut = packets;
        memset(r->m_vecChannelsOut + r->m_channelsAllocatedOut, 0, sizeof(RTMPPacket*) * (n - r->m_channelsAllocatedOut));
        r->m_channelsAllocatedOut = n;
    }

    prevPacket = r->m_vecChannelsOut[packet->m_nChannel];
    if (prevPacket && packet->m_headerType != RTMP_PACKET_SIZE_LARGE)
    {
        /* compress a bit by using the prev packet's attributes */
        if (prevPacket->m_nBodySize == packet->m_nBodySize
            && prevPacket->m_packetType == packet->m_packetType
            && packet->m_headerType == RTMP_PACKET_SIZE_MEDIUM)
            packet->m_headerType = RTMP_PACKET_SIZE_SMALL;

        if (prevPacket->m_nTimeStamp == packet->m_nTimeStamp
            && packet->m_headerType == RTMP_PACKET_SIZE_SMALL)
            packet->m_headerType = RTMP_PACKET_SIZE_MINIMUM;
        last = prevPacket->m_nTimeStamp;
    }

    if (packet->m_headerType > 3)	/* sanity */
    {

        LOGI("RTMP_SendPacket",  "sanity failed!! trying to send header of type: 0x%02x.",
             (unsigned char)packet->m_headerType);
        return RTMP_ERROR_SANITY_FAIL;
    }

    nSize = packetSize[packet->m_headerType];
    hSize = nSize; cSize = 0;
    t = packet->m_nTimeStamp - last;

    if (packet->m_body)
    {
        header = packet->m_body - nSize;
        hend = packet->m_body;
    }
    else
    {
        header = hbuf + 6;
        hend = hbuf + sizeof(hbuf);
    }

    if (packet->m_nChannel > 319)
        cSize = 2;
    else if (packet->m_nChannel > 63)
        cSize = 1;
    if (cSize)
    {
        header -= cSize;
        hSize += cSize;
    }

    if (nSize > 1 && t >= 0xffffff)
    {
        header -= 4;
        hSize += 4;
    }

    hptr = header;
    c = packet->m_headerType << 6;
    switch (cSize)
    {
        case 0:
            c |= packet->m_nChannel;
            break;
        case 1:
            break;
        case 2:
            c |= 1;
            break;
    }
    *hptr++ = c;
    if (cSize)
    {
        int tmp = packet->m_nChannel - 64;
        *hptr++ = tmp & 0xff;
        if (cSize == 2)
            *hptr++ = tmp >> 8;
    }

    if (nSize > 1)
    {
        hptr = AMF_EncodeInt24(hptr, hend, t > 0xffffff ? 0xffffff : t);
    }

    if (nSize > 4)
    {
        hptr = AMF_EncodeInt24(hptr, hend, packet->m_nBodySize);
        *hptr++ = packet->m_packetType;
    }

    if (nSize > 8)
        hptr += EncodeInt32LE(hptr, packet->m_nInfoField2);

    if (nSize > 1 && t >= 0xffffff)
        hptr = AMF_EncodeInt32(hptr, hend, t);

    nSize = packet->m_nBodySize;
    buffer = packet->m_body;
    nChunkSize = r->m_outChunkSize;


    LOGI("RTMP_SendPacket",  "%s: fd=%d, size=%d", __FUNCTION__, r->m_sb.sb_socket,
         nSize);
    /* send all chunks in one HTTP request */
    if (r->Link.protocol & RTMP_FEATURE_HTTP)
    {
        int chunks = (nSize+nChunkSize-1) / nChunkSize;
        if (chunks > 1)
        {
            tlen = chunks * (cSize + 1) + nSize + hSize;
            tbuf = static_cast<char *>(malloc(tlen));
            if (!tbuf) {
                return RTMP_ERROR_MEM_ALLOC_FAIL;
            }
            toff = tbuf;
        }
    }
    while (nSize + hSize)
    {
        int wrote;

        if (nSize < nChunkSize)
            nChunkSize = nSize;

        if (tbuf)
        {
            memcpy(toff, header, nChunkSize + hSize);
            toff += nChunkSize + hSize;
        }
        else
        {
            wrote = WriteN(r, header, nChunkSize + hSize);
            if (!wrote) {
                return RTMP_ERROR_SEND_PACKET_FAIL;
            }
        }
        nSize -= nChunkSize;
        buffer += nChunkSize;
        hSize = 0;

        if (nSize > 0)
        {
            header = buffer - 1;
            hSize = 1;
            if (cSize)
            {
                header -= cSize;
                hSize += cSize;
            }
            *header = (0xc0 | c);
            if (cSize)
            {
                int tmp = packet->m_nChannel - 64;
                header[1] = tmp & 0xff;
                if (cSize == 2)
                    header[2] = tmp >> 8;
            }
        }
    }
    if (tbuf)
    {
        int wrote = WriteN(r, tbuf, toff-tbuf);
        free(tbuf);
        tbuf = NULL;
        if (!wrote) {
            return RTMP_ERROR_SEND_PACKET_FAIL;
        }
    }

    /* we invoked a remote method */
    if (packet->m_packetType == RTMP_PACKET_TYPE_INVOKE)
    {
        AVal method;
        char *ptr;
        ptr = packet->m_body + 1;
        AMF_DecodeString(ptr, &method);

        LOGI("RTMP_SendPacket",  "Invoking %s", method.av_val);
        /* keep it in call queue till result arrives */
        if (queue) {
            int txn;
            ptr += 3 + method.av_len;
            txn = (int)AMF_DecodeNumber(ptr);
            AV_queue(&r->m_methodCalls, &r->m_numCalls, &method, txn);
        }
    }

    if (!r->m_vecChannelsOut[packet->m_nChannel])
        r->m_vecChannelsOut[packet->m_nChannel] = static_cast<RTMPPacket *>(malloc(
                sizeof(RTMPPacket)));
    memcpy(r->m_vecChannelsOut[packet->m_nChannel], packet, sizeof(RTMPPacket));
    return RTMP_SUCCESS;
}



static RTMPResult SendFCUnpublish(RTMP *r){
    RTMPPacket packet;
    char pbuf[1024], *pend = pbuf + sizeof(pbuf);
    char *enc;

    packet.m_nChannel = 0x03;	/* control channel (invoke) */
    packet.m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    packet.m_packetType = RTMP_PACKET_TYPE_INVOKE;
    packet.m_nTimeStamp = 0;
    packet.m_nInfoField2 = 0;
    packet.m_hasAbsTimestamp = 0;
    packet.m_body = pbuf + RTMP_MAX_HEADER_SIZE;

    enc = packet.m_body;
    enc = AMF_EncodeString(enc, pend, &av_FCUnpublish);
    enc = AMF_EncodeNumber(enc, pend, ++r->m_numInvokes);
    *enc++ = AMF_NULL;
    enc = AMF_EncodeString(enc, pend, &r->Link.playpath);
    if (!enc)
        return static_cast<RTMPResult>(FALSE);

    packet.m_nBodySize = enc - packet.m_body;

    return RTMP_SendPacket(r, &packet, FALSE);
}
SAVC(deleteStream);
static RTMPResult SendDeleteStream(RTMP *r, double dStreamId)
{
    RTMPPacket packet;
    char pbuf[256], *pend = pbuf + sizeof(pbuf);
    char *enc;

    packet.m_nChannel = 0x03;	/* control channel (invoke) */
    packet.m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    packet.m_packetType = RTMP_PACKET_TYPE_INVOKE;
    packet.m_nTimeStamp = 0;
    packet.m_nInfoField2 = 0;
    packet.m_hasAbsTimestamp = 0;
    packet.m_body = pbuf + RTMP_MAX_HEADER_SIZE;

    enc = packet.m_body;
    enc = AMF_EncodeString(enc, pend, &av_deleteStream);
    enc = AMF_EncodeNumber(enc, pend, ++r->m_numInvokes);
    *enc++ = AMF_NULL;
    enc = AMF_EncodeNumber(enc, pend, dStreamId);

    packet.m_nBodySize = enc - packet.m_body;

    /* no response expected */
    return RTMP_SendPacket(r, &packet, FALSE);
}
static int HTTP_Post(RTMP *r, RTMPTCmd cmd, const char *buf, int len){
    char hbuf[512];
    int hlen = snprintf(hbuf, sizeof(hbuf), "POST /%s%s/%d HTTP/1.1\r\n"
                                            "Host: %.*s:%d\r\n"
                                            "Accept: */*\r\n"
                                            "User-Agent: Shockwave Flash\r\n"
                                            "Connection: Keep-Alive\r\n"
                                            "Cache-Control: no-cache\r\n"
                                            "Content-type: application/x-fcs\r\n"
                                            "Content-length: %d\r\n\r\n", RTMPT_cmds[cmd],
                        r->m_clientID.av_val ? r->m_clientID.av_val : "",
                        r->m_msgCounter, r->Link.hostname.av_len, r->Link.hostname.av_val,
                        r->Link.port, len);
    RTMPSockBuf_Send(&r->m_sb, hbuf, hlen);
    hlen = RTMPSockBuf_Send(&r->m_sb, buf, len);
    r->m_msgCounter++;
    r->m_unackd++;
    return hlen;
}
void RTMP_Close(RTMP *r){

}



RTMPResult RTMP_Connect0(RTMP *r, struct sockaddr * service){
    LOGI("RTMP_Connect0",  "CALLED");
    int on = 1;
    r->m_sb.sb_timedout = FALSE;
    r->m_pausing = 0;
    r->m_fDuration = 0.0;

    r->m_sb.sb_socket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (r->m_sb.sb_socket != -1)
    {
        //TODO:this is getting called
        LOGI("RTMP_Connect0",  "r->m_sb.sb_socket != -1");
        int err;
        struct timeval send_timeout;

        send_timeout.tv_sec = r->Link.sendTimeoutInMs / 1000;
        send_timeout.tv_usec = (r->Link.sendTimeoutInMs % 1000) * 1000;
        err = setsockopt(r->m_sb.sb_socket, SOL_SOCKET, SO_SNDTIMEO, &send_timeout, sizeof(send_timeout));
        if (err){
            LOGI("RTMP_Connect0",  "Error %d setting SO_SNDTIMEO", errno);
        }

        if (connect(r->m_sb.sb_socket, service, sizeof(struct sockaddr)) < 0)
        {
            LOGI("RTMP_Connect0",  "GetSockError()");
            int err = GetSockError();
            //TODO: I THINK THAT THIS IS THE ONE that will get called
            LOGI("RTMP_Connect0",  "%s, failed to connect socket. %d (%s)",
                 __FUNCTION__, err, strerror(err));
           // RTMP_Close(r);
            return RTMP_ERROR_SOCKET_CONNECT_FAIL;
        }

        if (r->Link.socksport)
        {

            LOGI("RTMP_Connect0",  "%s ... SOCKS negotiation", __FUNCTION__);
//            if (!SocksNegotiate(r))
//            {
//
//                LOGI("RTMP_Connect0", "%s, SOCKS negotiation failed.", __FUNCTION__);
//                RTMP_Close(r);
//                return RTMP_ERROR_SOCKS_NEGOTIATION_FAIL;
//            }
        }
    }
    else{
        LOGI("RTMP_Connect0", "%s, failed to create socket. Error: %d", __FUNCTION__,
             GetSockError());

        return RTMP_ERROR_SOCKET_CREATE_FAIL;
    }

    /* set timeout */
    {
        struct timeval tv;

        tv.tv_sec = r->Link.receiveTimeoutInMs / 1000;
        tv.tv_usec = (r->Link.receiveTimeoutInMs % 1000) * 1000;
        if (setsockopt
                (r->m_sb.sb_socket, SOL_SOCKET, SO_RCVTIMEO, (char *)&tv, sizeof(tv)))
        {

            LOGI("RTMP_Connect0", "%s, Setting socket timeout to %dms failed!",__FUNCTION__, r->Link.receiveTimeoutInMs);
        }
    }

    setsockopt(r->m_sb.sb_socket, IPPROTO_TCP, TCP_NODELAY, (char *) &on, sizeof(on));
    LOGI("RTMP_Connect0",  "RTMP_SUCCESS");

    return RTMP_SUCCESS;
}
uint32_t RTMP_GetTime(){
#ifdef _DEBUG
    return 0;
#elif defined(_WIN32)
    return timeGetTime();
#else
    struct tms t;
    if (!clk_tck) clk_tck = sysconf(_SC_CLK_TCK);
    return times(&t) * 1000 / clk_tck;
#endif
}


int RTMPSockBuf_Send(RTMPSockBuf *sb, const char *buf, int len){
    int rc;

    rc = send(sb->sb_socket, buf, len, 0);

    return rc;
}

int RTMPSockBuf_Fill(RTMPSockBuf *sb){
    int nBytes;

    if (!sb->sb_size)
        sb->sb_start = sb->sb_buf;

    while (1)
    {
        nBytes = sizeof(sb->sb_buf) - 1 - sb->sb_size - (sb->sb_start - sb->sb_buf);
#if defined(CRYPTO) && !defined(NO_SSL)
        if (sb->sb_ssl)
	{
	  nBytes = TLS_read(sb->sb_ssl, sb->sb_start + sb->sb_size, nBytes);
	}
      else
#endif
        {
            nBytes = recv(sb->sb_socket, sb->sb_start + sb->sb_size, nBytes, 0);
        }
        if (nBytes != -1)
        {
            sb->sb_size += nBytes;
        }
        else
        {
            int sockerr = GetSockError();

            LOGI("RTMP_ParseURL",  "%s, recv returned %d. GetSockError(): %d (%s)",__FUNCTION__, nBytes, sockerr, strerror(sockerr));
            if (sockerr == EINTR && !RTMP_ctrlC)
                continue;

            if (sockerr == EWOULDBLOCK || sockerr == EAGAIN)
            {
                sb->sb_timedout = TRUE;
                nBytes = 0;
            }
        }
        break;
    }

    return nBytes;
}

static int HTTP_read(RTMP *r, int fill){
    char *ptr;
    int hlen;

    restart:
    if (fill)
        RTMPSockBuf_Fill(&r->m_sb);
    if (r->m_sb.sb_size < 13) {
        if (fill)
            goto restart;
        return -2;
    }
    if (strncmp(r->m_sb.sb_start, "HTTP/1.1 200 ", 13))
        return -1;
    r->m_sb.sb_start[r->m_sb.sb_size] = '\0';
    if (!strstr(r->m_sb.sb_start, "\r\n\r\n")) {
        if (fill)
            goto restart;
        return -2;
    }

    ptr = r->m_sb.sb_start + sizeof("HTTP/1.1 200");
    while ((ptr = strstr(ptr, "Content-"))) {
        if (!strncasecmp(ptr+8, "length:", 7)) break;
        ptr += 8;
    }
    if (!ptr)
        return -1;
    hlen = atoi(ptr+16);
    ptr = strstr(ptr+16, "\r\n\r\n");
    if (!ptr)
        return -1;
    ptr += 4;
    if (ptr + (r->m_clientID.av_val ? 1 : hlen) > r->m_sb.sb_start + r->m_sb.sb_size)
    {
        if (fill)
            goto restart;
        return -2;
    }
    r->m_sb.sb_size -= ptr - r->m_sb.sb_start;
    r->m_sb.sb_start = ptr;
    r->m_unackd--;

    if (!r->m_clientID.av_val)
    {
        r->m_clientID.av_len = hlen;
        r->m_clientID.av_val = static_cast<char *>(malloc(hlen + 1));
        if (!r->m_clientID.av_val)
            return -1;
        r->m_clientID.av_val[0] = '/';
        memcpy(r->m_clientID.av_val+1, ptr, hlen-1);
        r->m_clientID.av_val[hlen] = 0;
        r->m_sb.sb_size = 0;
    }
    else
    {
        r->m_polling = *ptr++;
        r->m_resplen = hlen - 1;
        r->m_sb.sb_start++;
        r->m_sb.sb_size--;
    }
    return 0;
}
 RTMPResult SendBytesReceived(RTMP *r)
{
    RTMPPacket packet;
    char pbuf[256], *pend = pbuf + sizeof(pbuf);

    packet.m_nChannel = 0x02;	/* control channel (invoke) */
    packet.m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    packet.m_packetType = RTMP_PACKET_TYPE_BYTES_READ_REPORT;
    packet.m_nTimeStamp = 0;
    packet.m_nInfoField2 = 0;
    packet.m_hasAbsTimestamp = 0;
    packet.m_body = pbuf + RTMP_MAX_HEADER_SIZE;

    packet.m_nBodySize = 4;

    AMF_EncodeInt32(packet.m_body, pend, r->m_nBytesIn);	/* hard coded for now */
    r->m_nBytesInSent = r->m_nBytesIn;

    /*RTMP_Log(RTMP_LOGDEBUG, "Send bytes report. 0x%x (%d bytes)", (unsigned int)m_nBytesIn, m_nBytesIn); */
    return RTMP_SendPacket(r, &packet, FALSE);
}

static int ReadN(RTMP *r, char *buffer, int n)
{
    int nOriginalSize = n;
    int avail;
    char *ptr;

    r->m_sb.sb_timedout = FALSE;

#ifdef _DEBUG
    memset(buffer, 0, n);
#endif

    ptr = buffer;
    while (n > 0)
    {
        int nBytes = 0, nRead;
        if (r->Link.protocol & RTMP_FEATURE_HTTP)
        {
            int refill = 0;
            while (!r->m_resplen)
            {
                int ret;
                if (r->m_sb.sb_size < 13 || refill)
                {
//                    if (!r->m_unackd)
//                        HTTP_Post(r, RTMPT_IDLE, "", 1);
                    if (RTMPSockBuf_Fill(&r->m_sb) < 1)
                    {
                        if (!r->m_sb.sb_timedout)
                            RTMP_Close(r);
                        return 0;
                    }
                }
                if ((ret = HTTP_read(r, 0)) == -1){

                    LOGI("ReadN",  "%s, No valid HTTP response found", __FUNCTION__);
                    RTMP_Close(r);
                    return 0;
                }
                else if (ret == -2)
                {
                    refill = 1;
                }
                else
                {
                    refill = 0;
                }
            }
            if (r->m_resplen && !r->m_sb.sb_size)
                RTMPSockBuf_Fill(&r->m_sb);
            avail = r->m_sb.sb_size;
            if (avail > r->m_resplen)
                avail = r->m_resplen;
        }
        else
        {
            avail = r->m_sb.sb_size;
            if (avail == 0)
            {
                if (RTMPSockBuf_Fill(&r->m_sb) < 1)
                {
                    if (!r->m_sb.sb_timedout)
                        RTMP_Close(r);
                    return 0;
                }
                avail = r->m_sb.sb_size;
            }
        }
        nRead = ((n < avail) ? n : avail);
        if (nRead > 0 && r->m_sb.sb_start != NULL)
        {
            memcpy(ptr, r->m_sb.sb_start, nRead);
            r->m_sb.sb_start += nRead;
            r->m_sb.sb_size -= nRead;
            nBytes = nRead;
            r->m_nBytesIn += nRead;
            if (r->m_bSendCounter
                && r->m_nBytesIn > ( r->m_nBytesInSent + r->m_nClientBW / 10))
            {
                RTMPResult result = SendBytesReceived(r);
                if (result != RTMP_SUCCESS)
                    return result;
            }
        }
        /*RTMP_Log(RTMP_LOGDEBUG, "%s: %d bytes\n", __FUNCTION__, nBytes); */
#ifdef _DEBUG
        fwrite(ptr, 1, nBytes, netstackdump_read);
#endif

        if (nBytes == 0){

            LOGI("ReadN",  "%s, RTMP socket closed by peer", __FUNCTION__);
            /*goto again; */
            RTMP_Close(r);
            break;
        }

        if (r->Link.protocol & RTMP_FEATURE_HTTP)
            r->m_resplen -= nBytes;

#ifdef CRYPTO
        if (r->Link.rc4keyIn)
	{
	  RC4_encrypt(r->Link.rc4keyIn, nBytes, ptr);
	}
#endif

        n -= nBytes;
        ptr += nBytes;
    }

    return nOriginalSize - n;
}


static int WriteN(RTMP *r, const char *buffer, int n){
    const char *ptr = buffer;
#ifdef CRYPTO
    char *encrypted = 0;
  char buf[RTMP_BUFFER_CACHE_SIZE];

  if (r->Link.rc4keyOut)
    {
      if (n > sizeof(buf))
	encrypted = (char *)malloc(n);
      else
	encrypted = (char *)buf;
      ptr = encrypted;
      RC4_encrypt2(r->Link.rc4keyOut, n, buffer, ptr);
    }
#endif

    while (n > 0)
    {
        int nBytes;


        //this is actually going to send the bytes
        nBytes = RTMPSockBuf_Send(&r->m_sb, ptr, n);

        if (nBytes < 0){
            int sockerr = GetSockError();

            LOGI("WriteN",  "%s, RTMP send error %d (%d bytes)", __FUNCTION__,sockerr, n);
            n = 1;
            break;
        }

        if (nBytes == 0)
            break;

        n -= nBytes;
        ptr += nBytes;
    }

#ifdef CRYPTO
    if (encrypted && encrypted != buf)
    free(encrypted);
#endif

    return n == 0;
}


static int HandShake(RTMP *r, int FP9HandShake){
    int i;
    uint32_t uptime, suptime;
    int bMatch;
    char type;
    char clientbuf[RTMP_SIG_SIZE + 1], *clientsig = clientbuf + 1;
    char serversig[RTMP_SIG_SIZE];

    clientbuf[0] = 0x03;		/* not encrypted */

    uptime = htonl(RTMP_GetTime());
    memcpy(clientsig, &uptime, 4);

    memset(&clientsig[4], 0, 4);

#ifdef _DEBUG
    for (i = 8; i < RTMP_SIG_SIZE; i++)
    clientsig[i] = 0xff;
#else
    for (i = 8; i < RTMP_SIG_SIZE; i++)
        clientsig[i] = (char)(rand() & 255);
#endif

    if (!WriteN(r, clientbuf, RTMP_SIG_SIZE + 1))
        return FALSE;

    if (ReadN(r, &type, 1) != 1)	/* 0x03 or 0x06 */
        return FALSE;


    LOGI("HandShake",  "%s: Type Answer   : %02X", __FUNCTION__, type);

    if (type != clientbuf[0])
    LOGI("HandShake",  "%s: Type mismatch: client sent %d, server answered %d",__FUNCTION__, clientbuf[0], type);

    if (ReadN(r, serversig, RTMP_SIG_SIZE) != RTMP_SIG_SIZE)
        return FALSE;

    /* decode server response */

    memcpy(&suptime, serversig, 4);
    suptime = ntohl(suptime);


    LOGI("HandShake",  "%s: Server Uptime : %d", __FUNCTION__, suptime);
    LOGI("HandShake",  "%s: FMS Version   : %d.%d.%d.%d", __FUNCTION__,
         serversig[4], serversig[5], serversig[6], serversig[7]);

    /* 2nd part of handshake */
    if (!WriteN(r, serversig, RTMP_SIG_SIZE))
        return FALSE;

    if (ReadN(r, serversig, RTMP_SIG_SIZE) != RTMP_SIG_SIZE)
        return FALSE;

    bMatch = (memcmp(serversig, clientsig, RTMP_SIG_SIZE) == 0);
    if (!bMatch){
        LOGI("HandShake",  "%s, client signature does not match!", __FUNCTION__);

    }
    return TRUE;
}

#define SAVC(x)	static const AVal av_##x = AVC(#x)

SAVC(app);
SAVC(connect);
SAVC(flashVer);
SAVC(swfUrl);
SAVC(pageUrl);
SAVC(tcUrl);
SAVC(fpad);
SAVC(capabilities);
SAVC(audioCodecs);
SAVC(videoCodecs);
SAVC(videoFunction);
SAVC(objectEncoding);
SAVC(secureToken);
SAVC(secureTokenResponse);
SAVC(type);
SAVC(nonprivate);

 RTMPResult SendConnectPacket(RTMP *r, RTMPPacket *cp){
    RTMPPacket packet;
    char pbuf[4096], *pend = pbuf + sizeof(pbuf);
    char *enc;

    if (cp)
        return RTMP_SendPacket(r, cp, TRUE);

    packet.m_nChannel = 0x03;	/* control channel (invoke) */
    packet.m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet.m_packetType = RTMP_PACKET_TYPE_INVOKE;
    packet.m_nTimeStamp = 0;
    packet.m_nInfoField2 = 0;
    packet.m_hasAbsTimestamp = 0;
    packet.m_body = pbuf + RTMP_MAX_HEADER_SIZE;

    enc = packet.m_body;
    enc = AMF_EncodeString(enc, pend, &av_connect);
    enc = AMF_EncodeNumber(enc, pend, ++r->m_numInvokes);
    *enc++ = AMF_OBJECT;

    enc = AMF_EncodeNamedString(enc, pend, &av_app, &r->Link.app);
    if (!enc)
        return RTMP_ERROR_CONNECT_FAIL;
    if (r->Link.protocol & RTMP_FEATURE_WRITE)
    {
        enc = AMF_EncodeNamedString(enc, pend, &av_type, &av_nonprivate);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
    }
    if (r->Link.flashVer.av_len)
    {
        enc = AMF_EncodeNamedString(enc, pend, &av_flashVer, &r->Link.flashVer);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
    }
    if (r->Link.swfUrl.av_len)
    {
        enc = AMF_EncodeNamedString(enc, pend, &av_swfUrl, &r->Link.swfUrl);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
    }
    if (r->Link.tcUrl.av_len)
    {
        enc = AMF_EncodeNamedString(enc, pend, &av_tcUrl, &r->Link.tcUrl);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
    }
    if (!(r->Link.protocol & RTMP_FEATURE_WRITE))
    {
        enc = AMF_EncodeNamedBoolean(enc, pend, &av_fpad, FALSE);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
        enc = AMF_EncodeNamedNumber(enc, pend, &av_capabilities, 15.0);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
        enc = AMF_EncodeNamedNumber(enc, pend, &av_audioCodecs, r->m_fAudioCodecs);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
        enc = AMF_EncodeNamedNumber(enc, pend, &av_videoCodecs, r->m_fVideoCodecs);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
        enc = AMF_EncodeNamedNumber(enc, pend, &av_videoFunction, 1.0);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
        if (r->Link.pageUrl.av_len)
        {
            enc = AMF_EncodeNamedString(enc, pend, &av_pageUrl, &r->Link.pageUrl);
            if (!enc)
                return RTMP_ERROR_CONNECT_FAIL;
        }
    }
    if (r->m_fEncoding != 0.0 || r->m_bSendEncoding)
    {	/* AMF0, AMF3 not fully supported yet */
        enc = AMF_EncodeNamedNumber(enc, pend, &av_objectEncoding, r->m_fEncoding);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
    }
    if (enc + 3 >= pend)
        return RTMP_ERROR_CONNECT_FAIL;
    *enc++ = 0;
    *enc++ = 0;			/* end of object - 0x00 0x00 0x09 */
    *enc++ = AMF_OBJECT_END;

    /* add auth string */
    if (r->Link.auth.av_len)
    {
        enc = AMF_EncodeBoolean(enc, pend, r->Link.lFlags & RTMP_LF_AUTH);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
        enc = AMF_EncodeString(enc, pend, &r->Link.auth);
        if (!enc)
            return RTMP_ERROR_CONNECT_FAIL;
    }
    if (r->Link.extras.o_num)
    {
        int i;
        for (i = 0; i < r->Link.extras.o_num; i++)
        {
            enc = AMFProp_Encode(&r->Link.extras.o_props[i], enc, pend);
            if (!enc)
                return RTMP_ERROR_CONNECT_FAIL;
        }
    }
    packet.m_nBodySize = enc - packet.m_body;

    return RTMP_SendPacket(r, &packet, TRUE);
}


RTMPResult RTMP_Connect1(RTMP *r, RTMPPacket *cp){
    if (r->Link.protocol & RTMP_FEATURE_SSL){
#if defined(CRYPTO) && !defined(NO_SSL)
        TLS_client(RTMP_TLS_ctx, r->m_sb.sb_ssl);
      TLS_setfd(r->m_sb.sb_ssl, r->m_sb.sb_socket);
      if (TLS_connect(r->m_sb.sb_ssl) < 0)
	{
	  RTMP_Log(RTMP_LOGERROR, "%s, TLS_Connect failed", __FUNCTION__);
	  RTMP_Close(r);
	  return FALSE;
	}
#else

        LOGI("RTMP_Connect1",  "%s, no SSL/TLS support", __FUNCTION__);
        RTMP_Close(r);
        return RTMP_ERROR_NO_SSL_TLS_SUPP;

#endif
    }


    LOGI("RTMP_Connect1",  "%s, ... connected, handshaking", __FUNCTION__);
    if (!HandShake(r, TRUE))
    {

        LOGI("RTMP_Connect1",  "%s, handshake failed.", __FUNCTION__);
        RTMP_Close(r);
        return RTMP_ERROR_HANDSHAKE_FAIL;
    }

    LOGI("RTMP_Connect1",  "%s, handshaked", __FUNCTION__);

    if (SendConnectPacket(r, cp) != RTMP_SUCCESS){

        LOGI("RTMP_Connect1", "%s, RTMP connect failed.", __FUNCTION__);
        RTMP_Close(r);
        return RTMP_ERROR_CONNECT_FAIL;
    }
    LOGI("RTMP_Connect1",  "RTMP_SUCCESS");
    return RTMP_SUCCESS;
}

RTMPResult RTMP_Connect(RTMP *r, RTMPPacket *cp){
    struct sockaddr_in service;
    RTMPResult ret = RTMP_SUCCESS;
    if (!r->Link.hostname.av_len)
        return RTMP_ERROR_URL_MISSING_PROTOCOL;

    memset(&service, 0, sizeof(struct sockaddr_in));
    service.sin_family = AF_INET;

    if (r->Link.socksport)
    {
        LOGI("RTMP_Connect0", "SOCKS");
        /* Connect via SOCKS */
        ret = add_addr_info(&service, &r->Link.sockshost, r->Link.socksport);
        if (ret != RTMP_SUCCESS){
            return ret;
        }
    }
    else{
        LOGI("RTMP_Connect0", "directly");
        /* Connect directly */
        ret = add_addr_info(&service, &r->Link.hostname, r->Link.port);
        if (ret != RTMP_SUCCESS)
        {
            LOGI("RTMP_Connect0", "RTMPResult --> %d",ret);
            return ret;
        }
    }
    LOGI("RTMP_Connect0", "RTMP_Connect0()");

    //THIS IS GETTING CALLED
    ret = RTMP_Connect0(r, (struct sockaddr *)&service);
    if (ret != RTMP_SUCCESS)
        return ret;

    r->m_bSendCounter = TRUE;

   // todo: return RTMP_Connect1(r, cp); IS THE ACTUAL RETURN VALUE. NOT RTMP_SUCCESS
    return RTMP_Connect1(r, cp);
  return RTMP_SUCCESS;
}


#define HOST "ingest.global-contribute.live-video.net"
#define PORT 443  // RTMP over TCP uses port 443 in your case

static int manualConnection() {
    int sockfd;
    struct sockaddr_in server_addr;
    struct hostent *server;

    // Create socket
    sockfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (sockfd < 0) {
        LOGI("mainTesting",  "Error creating socket");

    }

    // Get host by name
    server = gethostbyname(HOST);
    if (server == NULL) {
        LOGI("mainTesting",  "Error resolving hostname");
        close(sockfd);
        exit(1);
    }

    // Setup server address struct
    memset(&server_addr, 0, sizeof(server_addr)); //ensures that any parts of the structure that we do not explicitly set contain zero.
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    memcpy(&server_addr.sin_addr.s_addr, server->h_addr, server->h_length);

    // Connect to server
    if (connect(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {

        LOGI("mainTesting",  "Connection failed");
        close(sockfd);
        exit(1);
    }else{
        LOGI("mainTesting",  "Connected to RTMP server");
    }



    // Normally, you would now send an RTMP handshake request
    // Close socket when done
    close(sockfd);
    return 0;
}




//Now I need to make the JNI file
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_clicker_presentation_selfStreaming_RTMPNativeClient_nativeOpen(JNIEnv *env,jobject thiz,jstring url_,jboolean is_publish_mode,jlong rtmp_pointer,
                                                                                jint send_timeout_in_ms,
                                                                                jint receive_timeout_in_ms) {

// connecting url: rtmps://ingest.global-contribute.live-video.net:443/app
    //todo: I NEED TO RE-IMPLEMENT THIS METHOd
    //below is the method that is used to create a socket in the library
    manualConnection();
//  int sock1  = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
//    struct hostent *server;
//    server = gethostbyname("ingest.global-contribute.live-video.net");
//
//  //below is what the book tells me
//    unsigned long addr;
//    int sock2 = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
//    struct sockaddr_in service;
//    memset(&service, 0, sizeof(struct sockaddr_in));
//    service.sin_family = AF_INET;
//    service.sin_port = htons(443);
//    if(sock2<0){
//        LOGI("NATIVEOPEN",  "FAILED TO CREATE");
//    }else{
//        LOGI("NATIVEOPEN",  "SOCK CREATED");
//    }

//     char *url = "rtmps://ingest.global-contribute.live-video.net:443/app/";
////    RTMP *rtmp = (RTMP *) nullptr;
////     rtmp = RTMP_Alloc();
//    RTMP *rtmp = RTMP_Alloc();
//    if (rtmp == NULL) {
//
//        LOGI("NATIVEOPEN",  "RTMP open called without allocating rtmp object");
//        return RTMP_ERROR_IGNORED;
//    }
//
//    RTMP_Init(rtmp);
//    rtmp->Link.receiveTimeoutInMs = receiveTimeoutInMs;
//    rtmp->Link.sendTimeoutInMs = sendTimeoutInMs;
//    RTMPResult ret = RTMP_SetupURL(rtmp, url);
//
//    LOGI("NATIVEOPEN",  "RTMPResult--> %d",ret);
////
////    if (ret != RTMP_SUCCESS) {
////        RTMP_Free(rtmp);
////        return ret;
////    }
//    if (is_publish_mode) {
//        RTMP_EnableWrite(rtmp);
//    }

   // ret = RTMP_Connect(rtmp, NULL);
//    if (ret != RTMP_SUCCESS) {
//        RTMP_Free(rtmp);
//        return ret;
//    }
//    ret = RTMP_ConnectStream(rtmp, 0);
//
//    if (ret != RTMP_SUCCESS) {
//        RTMP_Free(rtmp);
//        return ret;
//    }
//    (*env)->ReleaseStringUTFChars(env, url_, url);
//    return RTMP_SUCCESS;
    return -1;
}


