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


#include <stdarg.h>

#include <assert.h>
#include <ctype.h>
#include <unistd.h>


#include <errno.h>
#define GetSockError() errno


#include "rtmp_client.h"
RTMP_LogLevel RTMP_debuglevel = RTMP_LOGERROR;



#define RTMP_DEFAULT_CHUNKSIZE	128

#define OFF(x)	offsetof(struct RTMP,x)
#define TRUE	1
#define FALSE	0
#undef closesocket
#define closesocket(s)	close(s)


int RTMP_ctrlC;

static const char *RTMPT_cmds[] = {
        "open",
        "send",
        "idle",
        "close"
};
#define SAVC(x)	static const AVal av_##x = AVC(#x)


//todo: read up on arrays
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
static const int packetSize[] = { 12, 8, 4, 1 };





void RTMP_Init(RTMP *r){


    //memset(r, 0, sizeof(RTMP));
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

enum { OPT_STR=0, OPT_INT, OPT_BOOL, OPT_CONN };
#define AVC(str) {str, sizeof(str) - 1}




static const char hexdig[] = "0123456789abcdef";
static const AVal truth[] = {
        AVC("1"),
        AVC("on"),
        AVC("yes"),
        AVC("true"),
        {0, 0}
};
static const char *optinfo[] = {
        "string", "integer", "boolean", "AMF" };
static struct urlopt {
    AVal name;
    off_t off;
    int otype;
    int omisc;
    const char *use;
} options[] = {
        { AVC("socks"),     OFF(Link.sockshost),     OPT_STR, 0,
                "Use the specified SOCKS proxy" },
        { AVC("app"),       OFF(Link.app),           OPT_STR, 0,
                "Name of target app on server" },
        { AVC("tcUrl"),     OFF(Link.tcUrl),         OPT_STR, 0,
                "URL to played stream" },
        { AVC("pageUrl"),   OFF(Link.pageUrl),       OPT_STR, 0,
                "URL of played media's web page" },
        { AVC("swfUrl"),    OFF(Link.swfUrl),        OPT_STR, 0,
                "URL to player SWF file" },
        { AVC("flashver"),  OFF(Link.flashVer),      OPT_STR, 0,
                "Flash version string (default )" },
        { AVC("conn"),      OFF(Link.extras),        OPT_CONN, 0,
                "Append arbitrary AMF data to Connect message" },
        { AVC("playpath"),  OFF(Link.playpath),      OPT_STR, 0,
                "Path to target media on server" },
        { AVC("playlist"),  OFF(Link.lFlags),        OPT_BOOL, RTMP_LF_PLST,
                "Set playlist before play command" },
        { AVC("live"),      OFF(Link.lFlags),        OPT_BOOL, RTMP_LF_LIVE,
                "Stream is live, no seeking possible" },
        { AVC("subscribe"), OFF(Link.subscribepath), OPT_STR, 0,
                "Stream to subscribe to" },
        { AVC("jtv"), OFF(Link.usherToken),          OPT_STR, 0,
                "Justin.tv authentication token" },
        { AVC("token"),     OFF(Link.token),	       OPT_STR, 0,
                "Key for SecureToken response" },
        { AVC("swfVfy"),    OFF(Link.lFlags),        OPT_BOOL, RTMP_LF_SWFV,
                "Perform SWF Verification" },
        { AVC("swfAge"),    OFF(Link.swfAge),        OPT_INT, 0,
                "Number of days to use cached SWF hash" },
        { AVC("start"),     OFF(Link.seekTime),      OPT_INT, 0,
                "Stream start position in milliseconds" },
        { AVC("stop"),      OFF(Link.stopTime),      OPT_INT, 0,
                "Stream stop position in milliseconds" },
        { AVC("buffer"),    OFF(m_nBufferMS),        OPT_INT, 0,
                "Buffer time in milliseconds" },
        { AVC("timeout"),   OFF(Link.receiveTimeoutInMs),       OPT_INT, 0,
                "Session timeout in seconds" },
        { AVC("pubUser"),   OFF(Link.pubUser),       OPT_STR, 0,
                "Publisher username" },
        { AVC("pubPasswd"), OFF(Link.pubPasswd),     OPT_STR, 0,
                "Publisher password" },
        { {NULL,0}, 0, 0}
};
SAVC(FCUnpublish);
SAVC(deleteStream);
void AMF_AddProp(AMFObject *obj, const AMFObjectProperty *prop){
    if (!(obj->o_num & 0x0f))
    obj->o_props =
            static_cast<AMFObjectProperty *>(realloc(obj->o_props, (obj->o_num + 16) *
                                                                   sizeof(AMFObjectProperty)));
    memcpy(&obj->o_props[obj->o_num++], prop, sizeof(AMFObjectProperty));
}
static void RTMP_OptUsage(){
    int i;

    LOGI("RTMP_OptUsage", "Valid RTMP options are:");
    for (i=0; options[i].name.av_len; i++) {
        LOGI("RTMP_OptUsage", "%10s %-7s  %s\n", options[i].name.av_val,
             optinfo[options[i].otype], options[i].use);
    }
}
static int
parseAMF(AMFObject *obj, AVal *av, int *depth)
{
    AMFObjectProperty prop = {{0,0}};
    int i;
    char *p, *arg = const_cast<char *>(av->av_val);

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
void RTMP_Log(int level, const char *format, ...){
    va_list args;
    va_start(args, format);

    va_end(args);
}
void RTMP_LogHexString(int level, const uint8_t *data, unsigned long len){
#define BP_OFFSET 9
#define BP_GRAPH 60
#define BP_LEN	80
    char	line[BP_LEN];
    unsigned long i;

    if ( !data || level > RTMP_debuglevel )
        return;

    /* in case len is zero */
    line[0] = '\0';

    for ( i = 0 ; i < len ; i++ ) {
        int n = i & 15;
        unsigned off;

        if( !n ) {
            if( i ) RTMP_Log( level, "%s", line );
            memset( line, ' ', sizeof(line)-2 );
            line[sizeof(line)-2] = '\0';

            off = i % 0x0ffffU;

            line[2] = hexdig[0x0f & (off >> 12)];
            line[3] = hexdig[0x0f & (off >>  8)];
            line[4] = hexdig[0x0f & (off >>  4)];
            line[5] = hexdig[0x0f & off];
            line[6] = ':';
        }

        off = BP_OFFSET + n*3 + ((n >= 8)?1:0);
        line[off] = hexdig[0x0f & ( data[i] >> 4 )];
        line[off+1] = hexdig[0x0f & data[i]];

        off = BP_GRAPH + n + ((n >= 8)?1:0);

        if ( isprint( data[i] )) {
            line[BP_GRAPH + n] = data[i];
        } else {
            line[BP_GRAPH + n] = '.';
        }
    }

    RTMP_Log( level, "%s", line );
}
int RTMPSockBuf_Fill(RTMPSockBuf *sb){
    int nBytes;

    if (!sb->sb_size)
        sb->sb_start = sb->sb_buf;

    while (1){
        nBytes = sizeof(sb->sb_buf) - 1 - sb->sb_size - (sb->sb_start - sb->sb_buf);

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


            LOGI("RTMPSockBuf_Fill", "%s, recv returned %d. GetSockError(): %d (%s)",
                 __FUNCTION__, nBytes, sockerr, strerror(sockerr));
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
static int HTTP_read(RTMP *r, int fill) {
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
char *AMF_EncodeInt24(char *output, char *outend, int nVal){
    if (output+3 > outend)
        return NULL;

    output[2] = nVal & 0xff;
    output[1] = nVal >> 8;
    output[0] = nVal >> 16;
    return output+3;
}
char *AMF_EncodeInt32(char *output, char *outend, int nVal){
    if (output+4 > outend)
        return NULL;

    output[3] = nVal & 0xff;
    output[2] = nVal >> 8;
    output[1] = nVal >> 16;
    output[0] = nVal >> 24;
    return output+4;
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
static void
AV_queue(RTMP_METHOD **vals, int *num, AVal *av, int txn)
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
        RTMP_Log(RTMP_LOGERROR, "sanity failed!! trying to send header of type: 0x%02x.",
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

    RTMP_Log(RTMP_LOGDEBUG2, "%s: fd=%d, size=%d", __FUNCTION__, r->m_sb.sb_socket,
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

        RTMP_LogHexString(RTMP_LOGDEBUG2, (uint8_t *)header, hSize);
        RTMP_LogHexString(RTMP_LOGDEBUG2, (uint8_t *)buffer, nChunkSize);
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
        RTMP_Log(RTMP_LOGDEBUG, "Invoking %s", method.av_val);
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
RTMPResult SendBytesReceived(RTMP *r){
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
int
HTTP_Post(RTMP *r, RTMPTCmd cmd, const char *buf, int len)
{
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
int RTMP_IsConnected(RTMP *r){
    return r->m_sb.sb_socket != -1;
}
char *AMF_EncodeNumber(char *output, char *outend, double dVal)
{
    if (output+1+8 > outend)
        return NULL;

    *output++ = AMF_NUMBER;	/* type: Number */

#if __FLOAT_WORD_ORDER == __BYTE_ORDER
    #if __BYTE_ORDER == __BIG_ENDIAN
  memcpy(output, &dVal, 8);
#elif __BYTE_ORDER == __LITTLE_ENDIAN
  {
    unsigned char *ci, *co;
    ci = (unsigned char *)&dVal;
    co = (unsigned char *)output;
    co[0] = ci[7];
    co[1] = ci[6];
    co[2] = ci[5];
    co[3] = ci[4];
    co[4] = ci[3];
    co[5] = ci[2];
    co[6] = ci[1];
    co[7] = ci[0];
  }
#endif
#else
#if __BYTE_ORDER == __LITTLE_ENDIAN	/* __FLOAT_WORD_ORER == __BIG_ENDIAN */
    {
        unsigned char *ci, *co;
        ci = (unsigned char *)&dVal;
        co = (unsigned char *)output;
        co[0] = ci[3];
        co[1] = ci[2];
        co[2] = ci[1];
        co[3] = ci[0];
        co[4] = ci[7];
        co[5] = ci[6];
        co[6] = ci[5];
        co[7] = ci[4];
    }
#else /* __BYTE_ORDER == __BIG_ENDIAN && __FLOAT_WORD_ORER == __LITTLE_ENDIAN */
    {
    unsigned char *ci, *co;
    ci = (unsigned char *)&dVal;
    co = (unsigned char *)output;
    co[0] = ci[4];
    co[1] = ci[5];
    co[2] = ci[6];
    co[3] = ci[7];
    co[4] = ci[0];
    co[5] = ci[1];
    co[6] = ci[2];
    co[7] = ci[3];
  }
#endif
#endif

    return output+8;
}
char *AMF_EncodeInt16(char *output, char *outend, short nVal){
    if (output+2 > outend)
        return NULL;

    output[1] = nVal & 0xff;
    output[0] = nVal >> 8;
    return output+2;
}

char *AMF_EncodeString(char *output, char *outend, const AVal *bv){
    if ((bv->av_len < 65536 && output + 1 + 2 + bv->av_len > outend) ||
        output + 1 + 4 + bv->av_len > outend)
        return NULL;

    if (bv->av_len < 65536)
    {
        *output++ = AMF_STRING;

        output = AMF_EncodeInt16(output, outend, bv->av_len);
    }
    else
    {
        *output++ = AMF_LONG_STRING;

        output = AMF_EncodeInt32(output, outend, bv->av_len);
    }
    memcpy(output, bv->av_val, bv->av_len);
    output += bv->av_len;

    return output;
}



static RTMPResult SendFCUnpublish(RTMP *r)
{
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
    if (!enc){
        LOGI("SendFCUnpublish", "%s, No valid HTTP response found", enc);
    }



    packet.m_nBodySize = enc - packet.m_body;

    return RTMP_SendPacket(r, &packet, FALSE);
}
RTMPResult SendDeleteStream(RTMP *r, double dStreamId)
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
static void AV_clear(RTMP_METHOD *vals, int num){
    int i;
    for (i = 0; i < num; i++)
        free(vals[i].name.av_val);
    free(vals);
}
int RTMPSockBuf_Close(RTMPSockBuf *sb){
#if defined(CRYPTO) && !defined(NO_SSL)
    if (sb->sb_ssl)
    {
      TLS_shutdown(sb->sb_ssl);
      TLS_close(sb->sb_ssl);
      sb->sb_ssl = NULL;
    }
#endif
    if (sb->sb_socket != -1)
        return closesocket(sb->sb_socket);
    return 0;
}
void RTMPPacket_Free(RTMPPacket *p)
{
    if (p->m_body)
    {
        free(p->m_body - RTMP_MAX_HEADER_SIZE);
        p->m_body = NULL;
    }
}
void RTMP_Close(RTMP *r){
    int i;

    if (RTMP_IsConnected(r))
    {
        if (r->m_stream_id > 0)
        {
            i = r->m_stream_id;
            r->m_stream_id = 0;
            if ((r->Link.protocol & RTMP_FEATURE_WRITE))
                SendFCUnpublish(r);
            SendDeleteStream(r, i);
        }
        if (r->m_clientID.av_val)
        {
            HTTP_Post(r, RTMPT_CLOSE, "", 1);
            free(r->m_clientID.av_val);
            r->m_clientID.av_val = NULL;
            r->m_clientID.av_len = 0;
        }
        RTMPSockBuf_Close(&r->m_sb);
    }

    r->m_stream_id = -1;
    r->m_sb.sb_socket = -1;
    r->m_nBWCheckCounter = 0;
    r->m_nBytesIn = 0;
    r->m_nBytesInSent = 0;

    if (r->m_read.flags & RTMP_READ_HEADER) {
        free(r->m_read.buf);
        r->m_read.buf = NULL;
    }
    r->m_read.dataType = 0;
    r->m_read.flags = 0;
    r->m_read.status = 0;
    r->m_read.nResumeTS = 0;
    r->m_read.nIgnoredFrameCounter = 0;
    r->m_read.nIgnoredFlvFrameCounter = 0;

    r->m_write.m_nBytesRead = 0;
    RTMPPacket_Free(&r->m_write);

    for (i = 0; i < r->m_channelsAllocatedIn; i++)
    {
        if (r->m_vecChannelsIn[i])
        {
            RTMPPacket_Free(r->m_vecChannelsIn[i]);
            free(r->m_vecChannelsIn[i]);
            r->m_vecChannelsIn[i] = NULL;
        }
    }
    free(r->m_vecChannelsIn);
    r->m_vecChannelsIn = NULL;
    free(r->m_channelTimestamp);
    r->m_channelTimestamp = NULL;
    r->m_channelsAllocatedIn = 0;
    for (i = 0; i < r->m_channelsAllocatedOut; i++)
    {
        if (r->m_vecChannelsOut[i])
        {
            free(r->m_vecChannelsOut[i]);
            r->m_vecChannelsOut[i] = NULL;
        }
    }
    free(r->m_vecChannelsOut);
    r->m_vecChannelsOut = NULL;
    r->m_channelsAllocatedOut = 0;
    AV_clear(r->m_methodCalls, r->m_numCalls);
    r->m_methodCalls = NULL;
    r->m_numCalls = 0;
    r->m_numInvokes = 0;

    r->m_bPlaying = FALSE;
    r->m_sb.sb_size = 0;

    r->m_msgCounter = 0;
    r->m_resplen = 0;
    r->m_unackd = 0;

    if (r->Link.lFlags & RTMP_LF_FTCU)
    {
        free(r->Link.tcUrl.av_val);
        r->Link.tcUrl.av_val = NULL;
        r->Link.lFlags ^= RTMP_LF_FTCU;
    }

#ifdef CRYPTO
    if (!(r->Link.protocol & RTMP_FEATURE_WRITE) || (r->Link.pFlags & RTMP_PUB_CLEAN))
    {
      free(r->Link.playpath0.av_val);
      r->Link.playpath0.av_val = NULL;
    }
  if ((r->Link.protocol & RTMP_FEATURE_WRITE) &&
      (r->Link.pFlags & RTMP_PUB_CLEAN) &&
      (r->Link.pFlags & RTMP_PUB_ALLOC))
    {
      free(r->Link.app.av_val);
      r->Link.app.av_val = NULL;
      free(r->Link.tcUrl.av_val);
      r->Link.tcUrl.av_val = NULL;
    }
  if (r->Link.dh)
    {
      MDH_free(r->Link.dh);
      r->Link.dh = NULL;
    }
  if (r->Link.rc4keyIn)
    {
      RC4_free(r->Link.rc4keyIn);
      r->Link.rc4keyIn = NULL;
    }
  if (r->Link.rc4keyOut)
    {
      RC4_free(r->Link.rc4keyOut);
      r->Link.rc4keyOut = NULL;
    }
#else
    free(r->Link.playpath0.av_val);
    r->Link.playpath0.av_val = NULL;
#endif
}


int ReadN(RTMP *r, char *buffer, int n){
    int nOriginalSize = n;
    int avail;
    char *ptr;

    r->m_sb.sb_timedout = FALSE;

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
                    if (!r->m_unackd)
                        HTTP_Post(r, RTMPT_IDLE, "", 1);
                    if (RTMPSockBuf_Fill(&r->m_sb) < 1)
                    {
                        if (!r->m_sb.sb_timedout)
                            RTMP_Close(r);
                        return 0;
                    }
                }
                if ((ret = HTTP_read(r, 0)) == -1)
                {

                    LOGI("ReadN", "%s, No valid HTTP response found", __FUNCTION__);
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

        if (nBytes == 0)
        {

            LOGI("ReadN", "%s, RTMP socket closed by peer", __FUNCTION__);
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
int RTMP_SetOpt(RTMP *r, const AVal *opt, AVal *arg)
{
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

        LOGI("RTMP_SetOpt", "Unknown option %s", opt->av_val);
        RTMP_OptUsage();
        return RTMP_ERROR_UNKNOWN_RTMP_OPTION;
    }

    return RTMP_SUCCESS;
}
static void
SocksSetup(RTMP *r, AVal *sockshost){
    if (sockshost->av_len)
    {
        LOGI("SocksSetup", "sockshost->av_len");
        const char *socksport = strchr(sockshost->av_val, ':');
        char *hostname = strdup(sockshost->av_val);

        if (socksport)
            hostname[socksport - sockshost->av_val] = '\0';
        r->Link.sockshost.av_val = hostname;
        r->Link.sockshost.av_len = strlen(hostname);

        r->Link.socksport = socksport ? atoi(socksport + 1) : 1080;

        LOGI("SocksSetup", "Connecting via SOCKS proxy: %s:%d", r->Link.sockshost.av_val,
             r->Link.socksport);
    }else{
        r->Link.sockshost.av_val = NULL;
        r->Link.sockshost.av_len = 0;
        r->Link.socksport = 0;
    }
}

int RTMPSockBuf_Send(RTMPSockBuf *sb, const char *buf, int len)
{
    int rc;

    {
        rc = send(sb->sb_socket, buf, len, 0);
    }
    return rc;
}


static int WriteN(RTMP *r, const char *buffer, int n)
{
    const char *ptr = buffer;

    while (n > 0)
    {
        int nBytes;

        if (r->Link.protocol & RTMP_FEATURE_HTTP)
            nBytes = HTTP_Post(r, RTMPT_SEND, ptr, n);
        else
            nBytes = RTMPSockBuf_Send(&r->m_sb, ptr, n);
        /*RTMP_Log(RTMP_LOGDEBUG, "%s: %d\n", __FUNCTION__, nBytes); */

        if (nBytes < 0)
        {
            int sockerr = GetSockError();


            LOGI("WriteN", "%s, RTMP send error %d (%d bytes)", __FUNCTION__,
                 sockerr, n);

            if (sockerr == EINTR && !RTMP_ctrlC)
                continue;

            RTMP_Close(r);
            n = 1;
            break;
        }

        if (nBytes == 0)
            break;

        n -= nBytes;
        ptr += nBytes;
    }


    return n == 0;
}

RTMPResult RTMP_SetupURL(RTMP *r,  char *url){
    AVal opt, arg;
    char *p1, *p2, *ptr = strchr(url, ' ');
    int ret, len;
    unsigned int port = 0;



    len = strlen(url);
    ret = RTMP_ParseURL(url, &r->Link.protocol, &r->Link.hostname,
                        &port, &r->Link.playpath0, &r->Link.app);
    if (ret != RTMP_SUCCESS)
    {
        return (RTMPResult)ret;
    }
    r->Link.port = port;
    r->Link.playpath = r->Link.playpath0;

    //the while loop and new variables
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
            return (RTMPResult) ret;
    }
    if (!r->Link.tcUrl.av_len){
        r->Link.tcUrl.av_val = url;
        if (r->Link.app.av_len){
            if (r->Link.app.av_val < url + len){
                /* if app is part of original url, just use it */
                r->Link.tcUrl.av_len = r->Link.app.av_len + (r->Link.app.av_val - url);
            }else{
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
        }else{
            r->Link.tcUrl.av_len = strlen(url);
        }
    }
    //LOGI("add_addr_info", "HOST DNS. (addr: %s)", hostname);
    //todo: THIS IS NEXT THING TO DO
    LOGI("SocksSetup", "sockshost-%d", r->Link.sockshost.av_len); //r->Link.sockshost.av_len IS 0 BUT IT COULD BE A BUG
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
static RTMPResult add_addr_info(struct sockaddr_in *service, AVal *host, int port){
    char *hostname;
    RTMPResult ret = RTMP_SUCCESS;
    if (host->av_val[host->av_len]){
        hostname = static_cast<char *>(malloc(host->av_len + 1));
        memcpy(hostname, host->av_val, host->av_len);
        hostname[host->av_len] = '\0';
    }else{
        hostname = host->av_val;
    }

    service->sin_addr.s_addr = inet_addr(hostname);
    if (service->sin_addr.s_addr == INADDR_NONE){

        struct hostent *host = gethostbyname(hostname);
        if (host == nullptr || host->h_addr == nullptr){

            LOGI("add_addr_info", "Problem accessing the DNS. (addr: %s)", hostname);
            ret = RTMP_ERROR_DNS_NOT_REACHABLE;
            goto finish;
        }
        LOGI("add_addr_info", "HOST DNS. (addr: %s)", hostname);
        service->sin_addr = *(struct in_addr *)host->h_addr;
    }
    service->sin_port = htons(port);
    finish:
    if (hostname != host->av_val)
        free(hostname);
    return ret;
}


static int
SocksNegotiate(RTMP *r)
{
    unsigned long addr;
    struct sockaddr_in service;
    memset(&service, 0, sizeof(struct sockaddr_in));

    add_addr_info(&service, &r->Link.hostname, r->Link.port);
    addr = htonl(service.sin_addr.s_addr);

    {
        char packet[] = {
                4, 1,			/* SOCKS 4, connect */
                static_cast<char>((r->Link.port >> 8) & 0xFF),
                static_cast<char>((r->Link.port) & 0xFF),
                static_cast<char>((char)(addr >> 24) & 0xFF), static_cast<char>((char)(addr >> 16) & 0xFF),
                static_cast<char>((char)(addr >> 8) & 0xFF), static_cast<char>((char)addr & 0xFF),
                0
        };				/* NULL terminate */

        WriteN(r, packet, sizeof packet);

        if (ReadN(r, packet, 8) != 8)
            return FALSE;

        if (packet[0] == 0 && packet[1] == 90)
        {
            return TRUE;
        }
        else
        {

            LOGI("SocksNegotiate", "%s, SOCKS returned error code %d", __FUNCTION__, packet[1]);
            return FALSE;
        }
    }
}

RTMPResult RTMP_Connect0(RTMP *r, struct sockaddr * service){
    int on = 1;
    r->m_sb.sb_timedout = FALSE;
    r->m_pausing = 0;
    r->m_fDuration = 0.0;

    r->m_sb.sb_socket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (r->m_sb.sb_socket != -1)
    {
        int err;
        struct timeval send_timeout;

        send_timeout.tv_sec = r->Link.sendTimeoutInMs / 1000;
        send_timeout.tv_usec = (r->Link.sendTimeoutInMs % 1000) * 1000;
        err = setsockopt(r->m_sb.sb_socket, SOL_SOCKET, SO_SNDTIMEO, &send_timeout, sizeof(send_timeout));
        if (err){
            LOGI("RTMP_Connect0", "Error %d setting SO_SNDTIMEO", errno);

        }

        if (connect(r->m_sb.sb_socket, service, sizeof(struct sockaddr)) < 0){


            LOGI("RTMP_Connect0", "%s, failed to connect socket. %d (%s)",
                 __FUNCTION__, -1, strerror(err));
            //todo: THIS CLOSE FUNCTION NEEDS TO BE CREATED
            RTMP_Close(r);
            return RTMP_ERROR_SOCKET_CONNECT_FAIL;
        }

        if (r->Link.socksport){
            LOGI("RTMP_Connect0", "%s ... SOCKS negotiation", __FUNCTION__);

            if (!SocksNegotiate(r))
            {
                LOGI("RTMP_Connect0","%s SOCKS negotiation failed.", __FUNCTION__);

                RTMP_Close(r);
                return RTMP_ERROR_SOCKS_NEGOTIATION_FAIL;
            }
        }
    }
    else
    {

        LOGI("RTMP_Connect0","%s, failed to create socket. Error: %d", __FUNCTION__,
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
            LOGI("RTMP_Connect0","%s, Setting socket timeout to %dms failed!",
                 __FUNCTION__, r->Link.receiveTimeoutInMs);

        }
    }

    setsockopt(r->m_sb.sb_socket, IPPROTO_TCP, TCP_NODELAY, (char *) &on, sizeof(on));

    return RTMP_SUCCESS;
}

RTMPResult RTMP_Connect(RTMP *r, RTMPPacket *cp){
    struct sockaddr_in service;
    RTMPResult ret = RTMP_SUCCESS;
    if (!r->Link.hostname.av_len)
        return RTMP_ERROR_URL_MISSING_PROTOCOL;

    memset(&service, 0, sizeof(struct sockaddr_in));
    service.sin_family = AF_INET; //THIS MUST BE SET

    if (r->Link.socksport){
        LOGI("RTMP_Connect", "SOCKETS");
        /* Connect via SOCKS */
        ret = add_addr_info(&service, &r->Link.sockshost, r->Link.socksport);
        if (ret != RTMP_SUCCESS)
        {
            return ret;
        }
    }else{
        /* Connect directly */
        LOGI("RTMP_Connect", "DIRECTLY");
        ret = add_addr_info(&service, &r->Link.hostname, r->Link.port);
        if (ret != RTMP_SUCCESS)
        {
            LOGI("RTMP_Connect", "DIRECTLY");
            return ret;
        }
    }

    ret = RTMP_Connect0(r, (struct sockaddr *)&service);
    if (ret != RTMP_SUCCESS)
        return ret;

    r->m_bSendCounter = TRUE;

    return RTMP_Connect1(r, cp);
//todo: THIS RETURN VALUE IS JUST FOR TESTING
return RTMP_SUCCESS;

}





//Now I need to make the JNI file
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_clicker_presentation_selfStreaming_RTMPNativeClient_nativeOpen(JNIEnv *env,jobject thiz,jstring url_,jboolean is_publish_mode,jlong rtmp_pointer,
                                                                                jint send_timeout_in_ms,
                                                                                jint receive_timeout_in_ms) {

    RTMP *rtmp = new RTMP();
    RTMP_Init(rtmp);
    const char* url = env->GetStringUTFChars(url_, nullptr);

    rtmp->Link.receiveTimeoutInMs = receive_timeout_in_ms;
    rtmp->Link.sendTimeoutInMs = send_timeout_in_ms;

    //todo: STILL A LOT TO DO FOR SetupURL
    RTMPResult ret = RTMP_SetupURL(rtmp, "rtmps://ingest.global-contribute.live-video.net:443/app/");

    if (ret != RTMP_SUCCESS) {
        LOGI("NATIVEOPENMETHOD", "FAILED");
        return ret;
    }
    LOGI("NATIVEOPENMETHOD", "VALUE -->%d",ret);
    if (is_publish_mode) {
        //I think this is if the stream is already streaming
       // RTMP_EnableWrite(rtmp);
    }

    ret = RTMP_Connect(rtmp, NULL);


    return (int) ret;
}


