//
// Created by Tristan on 2025-02-07.
//






#ifndef CLICKER_RTMP_H
#define CLICKER_RTMP_H

#endif //CLICKER_RTMP_H
#define RTMP_MAX_HEADER_SIZE 18
#define RTMP_FEATURE_HTTP	0x01
#define RTMP_FEATURE_ENC	0x02
#define RTMP_FEATURE_SSL	0x04
#define RTMP_FEATURE_MFP	0x08	/* not yet supported */
#define RTMP_FEATURE_WRITE	0x10	/* publish, not play */
#define RTMP_FEATURE_HTTP2	0x20	/* server-side rtmpt */
/* needs to fit largest number of bytes recv() may return */
#define RTMP_BUFFER_CACHE_SIZE (16*1024)
#define RTMP_PROTOCOL_RTMP      0
#define RTMP_PROTOCOL_RTMPT     RTMP_FEATURE_HTTP
#define RTMP_PROTOCOL_RTMPS     RTMP_FEATURE_SSL
#define RTMP_PROTOCOL_RTMPTE    (RTMP_FEATURE_HTTP|RTMP_FEATURE_ENC)
#define RTMP_PROTOCOL_RTMPTS    (RTMP_FEATURE_HTTP|RTMP_FEATURE_SSL)
#define RTMP_PROTOCOL_RTMPE     RTMP_FEATURE_ENC
#define RTMP_PROTOCOL_RTMFP     RTMP_FEATURE_MFP

#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))

#define RTMP_MAX_HEADER_SIZE 18
#define RTMP_PACKET_SIZE_LARGE    0
#define RTMP_PACKET_SIZE_MEDIUM   1
#define RTMP_PACKET_SIZE_SMALL    2
#define RTMP_PACKET_SIZE_MINIMUM  3
#define RTMP_PACKET_TYPE_CHUNK_SIZE         0x01
/*      RTMP_PACKET_TYPE_...                0x02 */
#define RTMP_PACKET_TYPE_BYTES_READ_REPORT  0x03
#define RTMP_PACKET_TYPE_CONTROL            0x04
#define RTMP_PACKET_TYPE_SERVER_BW          0x05
#define RTMP_PACKET_TYPE_CLIENT_BW          0x06
#define RTMP_PACKET_TYPE_INVOKE             0x14






 struct AVal{
     char* av_val;
    int av_len;
} ;

struct RTMP_METHOD{
    AVal name;
    int num;
};
struct RTMPChunk
{
    int c_headerSize;
    int c_chunkSize;
    char *c_chunk;
    char c_header[RTMP_MAX_HEADER_SIZE];
};
struct RTMPPacket{
    uint8_t m_headerType;
    uint8_t m_packetType;
    uint8_t m_hasAbsTimestamp;	/* timestamp absolute or relative? */
    int m_nChannel;
    uint32_t m_nTimeStamp;	/* timestamp */
    int32_t m_nInfoField2;	/* last 4 bytes in a long header */
    uint32_t m_nBodySize;
    uint32_t m_nBytesRead;
    RTMPChunk *m_chunk;
    char *m_body;
};
/* state for read() wrapper */
 struct RTMP_READ{
    char *buf;
    char *bufpos;
    unsigned int buflen;
    uint32_t timestamp;
    uint8_t dataType;
    uint8_t flags;
#define RTMP_READ_HEADER	0x01
#define RTMP_READ_RESUME	0x02
#define RTMP_READ_NO_IGNORE	0x04
#define RTMP_READ_GOTKF		0x08
#define RTMP_READ_GOTFLVK	0x10
#define RTMP_READ_SEEKING	0x20
    int8_t status;
#define RTMP_READ_COMPLETE	-3
#define RTMP_READ_ERROR	-2
#define RTMP_READ_EOF	-1
#define RTMP_READ_IGNORE	0

    /* if bResume == TRUE */
    uint8_t initialFrameType;
    uint32_t nResumeTS;
    char *metaHeader;
    char *initialFrame;
    uint32_t nMetaHeaderSize;
    uint32_t nInitialFrameSize;
    uint32_t nIgnoredFrameCounter;
    uint32_t nIgnoredFlvFrameCounter;
};

struct RTMPSockBuf{
    int sb_socket;
    int sb_size;		/* number of unprocessed bytes in buffer */
    char *sb_start;		/* pointer into sb_pBuffer of next byte to process */
    char sb_buf[RTMP_BUFFER_CACHE_SIZE];	/* data read from socket */
    int sb_timedout;
    void *sb_ssl;
};
typedef enum
{ AMF_NUMBER = 0, AMF_BOOLEAN, AMF_STRING, AMF_OBJECT,
    AMF_MOVIECLIP,		/* reserved, not used */
    AMF_NULL, AMF_UNDEFINED, AMF_REFERENCE, AMF_ECMA_ARRAY, AMF_OBJECT_END,
    AMF_STRICT_ARRAY, AMF_DATE, AMF_LONG_STRING, AMF_UNSUPPORTED,
    AMF_RECORDSET,		/* reserved, not used */
    AMF_XML_DOC, AMF_TYPED_OBJECT,
    AMF_AVMPLUS,		/* switch to AMF3 */
    AMF_INVALID = 0xff
} AMFDataType;
struct AMFObject
{
    int o_num;
    struct AMFObjectProperty *o_props;
};
struct AMFObjectProperty
{
    AVal p_name;
    AMFDataType p_type;
    union
    {
        double p_number;
        AVal p_aval;
        AMFObject p_object;
    } p_vu;
    int16_t p_UTCoffset;
} ;



//todo: WHY DOES THIS NEED THE TYPEDEF?
 struct RTMP_LNK{
    AVal hostname;
    AVal sockshost;

    AVal playpath0;	/* parsed from URL */
    AVal playpath;	/* passed in explicitly */
    AVal tcUrl;
    AVal swfUrl;
    AVal pageUrl;
    AVal app;
    AVal auth;
    AVal flashVer;
    AVal subscribepath;
    AVal usherToken;
    AVal token;
    AVal pubUser;
    AVal pubPasswd;
    AMFObject extras;
    int edepth;

    int seekTime;
    int stopTime;

#define RTMP_LF_AUTH	0x0001	/* using auth param */
#define RTMP_LF_LIVE	0x0002	/* stream is live */
#define RTMP_LF_SWFV	0x0004	/* do SWF verification */
#define RTMP_LF_PLST	0x0008	/* send playlist before play */
#define RTMP_LF_BUFX	0x0010	/* toggle stream on BufferEmpty msg */
#define RTMP_LF_FTCU	0x0020	/* free tcUrl on close */
    int lFlags;

    int swfAge;

    int protocol;
    int receiveTimeoutInMs;
    int sendTimeoutInMs;

#define RTMP_PUB_NAME   0x0001  /* send login to server */
#define RTMP_PUB_RESP   0x0002  /* send salted password hash */
#define RTMP_PUB_ALLOC  0x0004  /* allocated data for new tcUrl & app */
#define RTMP_PUB_CLEAN  0x0008  /* need to free allocated data for newer tcUrl & app at exit */
#define RTMP_PUB_CLATE  0x0010  /* late clean tcUrl & app at exit */
    int pFlags;

    unsigned short socksport;
    unsigned short port;

#ifdef CRYPTO
    #define RTMP_SWF_HASHLEN	32
    void *dh;			/* for encryption */
    void *rc4keyIn;
    void *rc4keyOut;

    uint32_t SWFSize;
    uint8_t SWFHash[RTMP_SWF_HASHLEN];
    char SWFVerificationResponse[RTMP_SWF_HASHLEN+10];
#endif
};

struct RTMP{
    int m_inChunkSize;
    int m_outChunkSize;
    int m_nBWCheckCounter;
    int m_nBytesIn;
    int m_nBytesInSent;
    int m_nBufferMS;
    int m_stream_id;		/* returned in _result from createStream */
    int m_mediaChannel;
    uint32_t m_mediaStamp;
    uint32_t m_pauseStamp;
    int m_pausing;
    int m_nServerBW;
    int m_nClientBW;
    uint8_t m_nClientBW2;
    uint8_t m_bPlaying;
    uint8_t m_bSendEncoding;
    uint8_t m_bSendCounter;

    int m_numInvokes;
    int m_numCalls;
    RTMP_METHOD *m_methodCalls;	/* remote method calls queue */

    int m_channelsAllocatedIn;
    int m_channelsAllocatedOut;
    RTMPPacket **m_vecChannelsIn;
    RTMPPacket **m_vecChannelsOut;
    int *m_channelTimestamp;	/* abs timestamp of last packet */

    double m_fAudioCodecs;	/* audioCodecs for the connect packet */
    double m_fVideoCodecs;	/* videoCodecs for the connect packet */
    double m_fEncoding;		/* AMF0 or AMF3 */

    double m_fDuration;		/* duration of stream in seconds */

    int m_msgCounter;		/* RTMPT stuff */
    int m_polling;
    int m_resplen;
    int m_unackd;
    AVal m_clientID;

    RTMP_READ m_read;
    RTMPPacket m_write;
    RTMPSockBuf m_sb;
    RTMP_LNK Link;
};
typedef enum RTMPResult_ {
    RTMP_SUCCESS = 0,
    RTMP_READ_DONE = -1,
    RTMP_ERROR_OPEN_ALLOC = -2,
    RTMP_ERROR_OPEN_CONNECT_STREAM = -3,
    RTMP_ERROR_UNKNOWN_RTMP_OPTION = -4,
    RTMP_ERROR_UNKNOWN_RTMP_AMF_TYPE = -5,
    RTMP_ERROR_DNS_NOT_REACHABLE = -6,
    RTMP_ERROR_SOCKET_CONNECT_FAIL = -7,
    RTMP_ERROR_SOCKS_NEGOTIATION_FAIL = -8,
    RTMP_ERROR_SOCKET_CREATE_FAIL = -9,
    RTMP_ERROR_NO_SSL_TLS_SUPP = -10,
    RTMP_ERROR_HANDSHAKE_CONNECT_FAIL = -11,
    RTMP_ERROR_HANDSHAKE_FAIL = -12,
    RTMP_ERROR_CONNECT_FAIL = -13,
    RTMP_ERROR_CONNECTION_LOST = -14,
    RTMP_ERROR_KEYFRAME_TS_MISMATCH = -15,
    RTMP_ERROR_READ_CORRUPT_STREAM = -16,
    RTMP_ERROR_MEM_ALLOC_FAIL = -17,
    RTMP_ERROR_STREAM_BAD_DATASIZE = -18,
    RTMP_ERROR_PACKET_TOO_SMALL = -19,
    RTMP_ERROR_SEND_PACKET_FAIL = -20,
    RTMP_ERROR_AMF_ENCODE_FAIL = -21,
    RTMP_ERROR_URL_MISSING_PROTOCOL = -22,
    RTMP_ERROR_URL_MISSING_HOSTNAME = -23,
    RTMP_ERROR_URL_INCORRECT_PORT = -24,
    RTMP_ERROR_IGNORED = -25,
    RTMP_ERROR_GENERIC = -26,
    RTMP_ERROR_SANITY_FAIL = -27,
} RTMPResult;

typedef enum {
    RTMPT_OPEN=0, RTMPT_SEND, RTMPT_IDLE, RTMPT_CLOSE
} RTMPTCmd;

typedef enum { RTMP_LOGCRIT=0, RTMP_LOGERROR, RTMP_LOGWARNING, RTMP_LOGINFO,
    RTMP_LOGDEBUG, RTMP_LOGDEBUG2, RTMP_LOGALL
} RTMP_LogLevel;

void RTMP_Init(RTMP *r);
RTMP *RTMP_Alloc(void);
RTMPResult RTMP_SetupURL(RTMP *r,  char *url);
int RTMP_ParseURL( char *url, int *protocol, AVal *host,
                  unsigned int *port, AVal *playpath, AVal *app);
void AMF_AddProp(AMFObject * obj, const AMFObjectProperty * prop);
RTMPResult RTMP_Connect(RTMP *r, RTMPPacket *cp);
RTMPResult RTMP_Connect0(RTMP *r, struct sockaddr *svc);
void RTMP_Close(RTMP *r);
int ReadN(RTMP *r, char *buffer, int n);

//i need to remove the static and implement the actual function
static int HTTP_Post(RTMP *r,  RTMPTCmd cmd, const char *buf, int len);
RTMPResult SendBytesReceived(RTMP *r);
char *AMF_EncodeInt32(char *output, char *outend, int nVal);
RTMPResult RTMP_SendPacket(RTMP *r, RTMPPacket *packet, int queue);
char *AMF_EncodeInt24(char *output, char *outend, int nVal);
void RTMP_LogHexString(int level, const uint8_t *data, unsigned long len);
void RTMP_Log(int level, const char *format, ...) __attribute__ ((__format__ (__printf__, 2, 3)));
static int WriteN(RTMP *r, const char *buffer, int n);
void AMF_DecodeString(const char *data, AVal * str);
void AMF_DecodeString(const char *data, AVal * str);
double AMF_DecodeNumber(const char *data);
int RTMPSockBuf_Send(RTMPSockBuf *sb, const char *buf, int len);
int RTMP_IsConnected(RTMP *r);
char *AMF_EncodeNumber(char *output, char *outend, double dVal);
char *AMF_EncodeString(char *output, char *outend, const AVal * str);
char *AMF_EncodeInt16(char *output, char *outend, short nVal);
RTMPResult SendDeleteStream(RTMP *r, double dStreamId);
int RTMPSockBuf_Close(RTMPSockBuf *sb);
void RTMPPacket_Free(RTMPPacket *p);





