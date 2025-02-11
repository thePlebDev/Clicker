//
// Created by Tristan on 2025-02-08.
//

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../rtmp_client.h"


#include <android/log.h>

#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "TAG", __VA_ARGS__)
#define TRUE	1
#define FALSE	0

int RTMP_ParseURL( char *url, int *protocol, AVal *host, unsigned int *port,
                  AVal *app)
{
    char *p, *end, *col, /* *ques, */ *slash, *v6;



    LOGI("RTMP_ParseURL","Parsing...");

    *protocol = RTMP_PROTOCOL_RTMP;
    *port = 0;
    app->av_len = 0;
    app->av_val = NULL;

    /* Old School Parsing */

    /* look for usual :// pattern */
    p = strstr(url, "://");
    if(!p)
    {
        LOGI("RTMP_ParseURL","RTMP URL: No :// in url!");
        return FALSE;
    }
    {
        int len = (int)(p-url);

        if(len == 4 && strncasecmp(url, "rtmp", 4)==0)
            *protocol = RTMP_PROTOCOL_RTMP;
        else if(len == 5 && strncasecmp(url, "rtmpt", 5)==0)
            *protocol = RTMP_PROTOCOL_RTMPT;
        else if(len == 5 && strncasecmp(url, "rtmps", 5)==0)
            *protocol = RTMP_PROTOCOL_RTMPS;
        else if(len == 5 && strncasecmp(url, "rtmpe", 5)==0)
            *protocol = RTMP_PROTOCOL_RTMPE;
        else if(len == 5 && strncasecmp(url, "rtmfp", 5)==0)
            *protocol = RTMP_PROTOCOL_RTMFP;
        else if(len == 6 && strncasecmp(url, "rtmpte", 6)==0)
            *protocol = RTMP_PROTOCOL_RTMPTE;
        else if(len == 6 && strncasecmp(url, "rtmpts", 6)==0)
            *protocol = RTMP_PROTOCOL_RTMPTS;
        else
        {

            LOGI("RTMP_ParseURL","Unknown protocol!");
            goto parsehost;
        }
    }


    LOGI("RTMP_ParseURL","Parsed protocol: %d", *protocol);

    parsehost:
    /* let's get the hostname */
    p+=3;

    /* check for sudden death */
    if(*p==0)
    {

        LOGI("RTMP_ParseURL","No hostname in URL!");
        return FALSE;
    }

    end   = p + strlen(p);
    v6    = strchr(p, ']');
    // ques  = strchr(p, '?');
    slash = strchr(p, '/');
    col   = strchr((v6 && v6 < slash) ? v6 : p, ':');

    {
        int hostlen;
        if(slash)
            hostlen = slash - p;
        else
            hostlen = end - p;
        if(col && col -p < hostlen)
            hostlen = col - p;

        if(hostlen < 256){
            host->av_val = p;
            host->av_len = hostlen;

            LOGI("RTMP_ParseURL","Parsed host    : %.*s", hostlen, host->av_val);
        }
        else{

            LOGI("RTMP_ParseURL","Hostname exceeds 255 characters!");
        }

        p+=hostlen;
    }

    /* get the port number if available */
    if(*p == ':')
    {
        unsigned int p2;
        p++;
        p2 = atoi(p);
        if(p2 > 65535)
        {

            LOGI("RTMP_ParseURL","Invalid port number!");
        }
        else
        {
            *port = p2;
        }
    }

    if(!slash)
    {

        LOGI("RTMP_ParseURL","No application or playpath in URL!");
        return TRUE;
    }
    p = slash+1;

    //just..  whatever.
    app->av_val = p;
    app->av_len = (int)strlen(p);

    if(app->av_len && p[app->av_len-1] == '/')
        app->av_len--;


    LOGI("RTMP_ParseURL","Parsed app     : %.*s", app->av_len, p);
    p += app->av_len;

    if (*p == '/')
        p++;

    return TRUE;
}



