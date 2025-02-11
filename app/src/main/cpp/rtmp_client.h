//
// Created by Tristan on 2025-02-07.
//


#include <sys/socket.h>



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

#define MAX_OUTPUT_AUDIO_ENCODERS 6
#define MAX_OUTPUT_VIDEO_ENCODERS 6
#define RTMP_MAX_HEADER_SIZE 18
#define SOCKET int
enum audio_id_t {
    AUDIO_CODEC_NONE = 0,
    AUDIO_CODEC_AAC = 1,
};
enum video_id_t {
    CODEC_NONE = 0, // not valid in rtmp
    CODEC_H264 = 1, // legacy & Y2023 spec
    CODEC_AV1,      // Y2023 spec
    CODEC_HEVC,
};

typedef struct obs_output obs_output_t; //just creates an alias called `obs_output_t` for `struct obs_output`
typedef struct os_event_data os_event_t;
typedef struct os_sem_data os_sem_t;
struct dstr {
    char *array;
    size_t len; /* number of characters, excluding null terminator */
    size_t capacity;
};

enum {
    /**
     * Use if there's a problem that can potentially affect the program,
     * but isn't enough to require termination of the program.
     *
     * Use in creation functions and core subsystem functions.  Places that
     * should definitely not fail.
     */
    LOG_ERROR = 100,

    /**
     * Use if a problem occurs that doesn't affect the program and is
     * recoverable.
     *
     * Use in places where failure isn't entirely unexpected, and can
     * be handled safely.
     */
    LOG_WARNING = 200,

    /**
     * Informative message to be displayed in the log.
     */
    LOG_INFO = 300,

    /**
     * Debug message to be used mostly by developers.
     */
    LOG_DEBUG = 400
};


/* Double-ended Queue */
struct deque {
    void *data;
    size_t size;

    size_t start_pos;
    size_t end_pos;
    size_t capacity;
};
typedef struct AVal
{
    char *av_val;
    int av_len;
} AVal;
typedef struct RTMP_METHOD
{
    AVal name;
    int num;
} RTMP_METHOD;
typedef struct RTMPChunk
{
    int c_headerSize;
    int c_chunkSize;
    char *c_chunk;
    char c_header[RTMP_MAX_HEADER_SIZE];
} RTMPChunk;
typedef struct RTMPPacket
{
    uint8_t m_headerType;
    uint8_t m_packetType;
    uint8_t m_hasAbsTimestamp;	/* timestamp absolute or relative? */
    int m_nChannel;
    uint32_t m_nTimeStamp;	/* timestamp */
    uint32_t m_nLastWireTimeStamp; /* timestamp that was encoded when sending */
    int32_t m_nInfoField2;	/* last 4 bytes in a long header */
    uint32_t m_nBodySize;
    uint32_t m_nBytesRead;
    RTMPChunk *m_chunk;
    char *m_body;
} RTMPPacket;
/* state for read() wrapper */
typedef struct RTMP_READ
{
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
} RTMP_READ;
typedef enum
{
    AMF_NUMBER = 0, AMF_BOOLEAN, AMF_STRING, AMF_OBJECT,
    AMF_MOVIECLIP,		/* reserved, not used */
    AMF_NULL, AMF_UNDEFINED, AMF_REFERENCE, AMF_ECMA_ARRAY, AMF_OBJECT_END,
    AMF_STRICT_ARRAY, AMF_DATE, AMF_LONG_STRING, AMF_UNSUPPORTED,
    AMF_RECORDSET,		/* reserved, not used */
    AMF_XML_DOC, AMF_TYPED_OBJECT,
    AMF_AVMPLUS,		/* switch to AMF3 */
    AMF_INVALID = 0xff
}
        AMFDataType;

typedef struct AMFObject
{
    int o_num;
    struct AMFObjectProperty *o_props;
} AMFObject;
typedef struct AMFObjectProperty
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
} AMFObjectProperty;


typedef struct RTMPSockBuf
{
    struct sockaddr_storage sb_addr; /* address of remote  THIS COMES FROM <sys/socket.h>*/
    SOCKET sb_socket;
    int sb_size;		/* number of unprocessed bytes in buffer */
    char *sb_start;		/* pointer into sb_pBuffer of next byte to process */
    char sb_buf[RTMP_BUFFER_CACHE_SIZE];	/* data read from socket */
    int sb_timedout;
    void *sb_ssl;
} RTMPSockBuf;

typedef struct RTMP_Stream {
    int id;
    AVal playpath;
} RTMP_Stream;
typedef void (*CUSTOMCONNECTENCODING)(char **penc, char *ppend);
//START
typedef struct RTMP_LNK{
#define RTMP_MAX_STREAMS 8
    RTMP_Stream streams[RTMP_MAX_STREAMS];
    int nStreams;
    int curStreamIdx;
    int playingStreams;

    AVal hostname;
    AVal sockshost;

    CUSTOMCONNECTENCODING customConnectEncode;

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
    int receiveTimeout;	/* connection receive timeout in seconds */
    int sendTimeout;	/* connection send timeout in seconds */

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
        uint32_t SWFSize;
        uint8_t SWFHash[RTMP_SWF_HASHLEN];
        char SWFVerificationResponse[RTMP_SWF_HASHLEN+10];
#endif
} RTMP_LNK;

//  END
typedef struct RTMP_BINDINFO
{
    struct sockaddr_storage addr;
    int addrLen;
} RTMP_BINDINFO;

typedef int (*CUSTOMSEND)(RTMPSockBuf*, const char *, int, void*);
typedef struct RTMP
{
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

    uint8_t m_bUseNagle;
    uint8_t m_bCustomSend;
    void*   m_customSendParam;
    CUSTOMSEND m_customSendFunc;

    RTMP_BINDINFO m_bindIP;

    uint8_t m_bSendChunkSizeInfo;

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
    int connect_time_ms;
    int last_error_code;

#ifdef CRYPTO
    TLS_CTX RTMP_TLS_ctx;
#endif
} RTMP;


struct rtmp_stream {
    obs_output_t *output;

    pthread_mutex_t packets_mutex;
    struct deque packets;
    bool sent_headers;

    bool got_first_packet;
    int64_t start_dts_offset;

    volatile bool connecting;
    pthread_t connect_thread;

    volatile bool active;
    volatile bool disconnected;
    volatile bool encode_error;
    pthread_t send_thread;

    int max_shutdown_time_sec;

    os_sem_t *send_sem;
    os_event_t *stop_event;
    uint64_t stop_ts;
    uint64_t shutdown_timeout_ts;

    struct dstr path, key;
    struct dstr username, password;
    struct dstr encoder_name;
    struct dstr bind_ip;
    socklen_t addrlen_hint; /* hint IPv4 vs IPv6 */

    /* frame drop variables */
    int64_t drop_threshold_usec;
    int64_t pframe_drop_threshold_usec;
    int min_priority;
    float congestion;

    int64_t last_dts_usec;

    uint64_t total_bytes_sent;
    int dropped_frames;

#ifdef TEST_FRAMEDROPS
    struct deque droptest_info;
	uint64_t droptest_last_key_check;
	size_t droptest_max;
	size_t droptest_size;
#endif

    pthread_mutex_t dbr_mutex;
    struct deque dbr_frames;
    size_t dbr_data_size;
    uint64_t dbr_inc_timeout;
    long audio_bitrate;
    long dbr_est_bitrate;
    long dbr_orig_bitrate;
    long dbr_prev_bitrate;
    long dbr_cur_bitrate;
    long dbr_inc_bitrate;
    bool dbr_enabled;

    enum audio_id_t audio_codec[MAX_OUTPUT_AUDIO_ENCODERS];
    enum video_id_t video_codec[MAX_OUTPUT_VIDEO_ENCODERS];

    RTMP rtmp;

    bool new_socket_loop;
    bool low_latency_mode;
    bool disable_send_window_optimization;
    bool socket_thread_active;
    pthread_t socket_thread;
    uint8_t *write_buf;
    size_t write_buf_len;
    size_t write_buf_size;
    pthread_mutex_t write_buf_mutex;
    os_event_t *buffer_space_available_event;
    os_event_t *buffer_has_data_event;
    os_event_t *socket_available_event;
    os_event_t *send_thread_signaled_exit;
};

void RTMP_TLS_Free(RTMP *r);
void RTMP_Reset(RTMP *r);
int RTMP_SetupURL(RTMP *r, char *url);
int RTMP_ParseURL( char *url, int *protocol, AVal *host,
                  unsigned int *port, AVal *app);
void RTMP_EnableWrite(RTMP *r);







