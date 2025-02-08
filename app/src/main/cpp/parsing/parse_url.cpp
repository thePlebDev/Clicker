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

 enum RTMP_LogLevel
{ RTMP_LOGCRIT=0, RTMP_LOGERROR, RTMP_LOGWARNING, RTMP_LOGINFO,
    RTMP_LOGDEBUG, RTMP_LOGDEBUG2, RTMP_LOGALL
};

int RTMP_ParseURL( char *url, int *protocol, AVal *host, unsigned int *port,
                  AVal *playpath, AVal *app)
{
     char *end, *col, *ques, *slash;
     char *p;

    LOGI("RTMP_ParseURL", "Parsing...");

    *protocol = RTMP_PROTOCOL_RTMP;
    *port = 0;
    playpath->av_len = 0;
    playpath->av_val = NULL;
    app->av_len = 0;
    app->av_val = NULL;

    /* Old School Parsing */

   // /* look for usual :// pattern */
    p = strstr(url, "://"); //returns the first occurrence of :// in url
    //    LOGI("RTMP_ParseURL", "RTMP URL--> %s",url);
    if(!p) {
        LOGI("RTMP_ParseURL", "RTMP URL: No :// in url!");
        return RTMP_ERROR_URL_MISSING_PROTOCOL;
    }
    else{
        int len = (int)(p - url);

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
        else {

            LOGI("RTMP_ParseURL", "Unknown protocol!");
            goto parsehost;
        }
    }

    LOGI("RTMP_ParseURL", "Parsed protocol: %d", *protocol);


    parsehost:
    /* let's get the hostname */
    p+=3;

    /* check for sudden death */
    if(*p==0) {
        LOGI("RTMP_ParseURL", "No hostname in URL!");
        return RTMP_ERROR_URL_MISSING_HOSTNAME;
    }

    end   = p + strlen(p);
    col   = strchr(p, ':');
    ques  = strchr(p, '?');
    slash = strchr(p, '/');


    {
        int hostlen;
        if(slash)
            hostlen = slash - p;
        else
            hostlen = end - p;
        if(col && col -p < hostlen)
            hostlen = col - p;

        if(hostlen < 256) {
            host->av_val = p;
            host->av_len = hostlen;

            LOGI("RTMP_ParseURL", "Parsed host    : %.*s", hostlen, host->av_val);
        } else {

            LOGI("RTMP_ParseURL", "Hostname exceeds 255 characters!");
        }

        p+=hostlen;
    }

    /* get the port number if available */
    if(*p == ':') {
        unsigned int p2;
        p++;
        p2 = atoi(p);
        if(p2 > 65535) {

            LOGI("RTMP_ParseURL", "Invalid port number!");
        } else {
            *port = p2;
            LOGI("RTMP_ParseURL", "port -> %u",*port);
        }
    }

    if(!slash) {

        LOGI("RTMP_ParseURL","No application or playpath in URL!");
        return RTMP_SUCCESS;
    }
    p = slash+1;

    {
        /* parse application
         *
         * rtmp://host[:port]/app[/appinstance][/...]
         * application = app[/appinstance]
         */

        char *slash2, *slash3 = NULL, *slash4 = NULL;
        int applen, appnamelen;

        slash2 = strchr(p, '/');
        if(slash2)
            slash3 = strchr(slash2+1, '/');
        if(slash3)
            slash4 = strchr(slash3+1, '/');

        applen = end-p; /* ondemand, pass all parameters as app */
        appnamelen = applen; /* ondemand length */

        if(ques && strstr(p, "slist=")) { /* whatever it is, the '?' and slist= means we need to use everything as app and parse plapath from slist= */
            appnamelen = ques-p;
        }
        else if(strncmp(p, "ondemand/", 9)==0) {
            /* app = ondemand/foobar, only pass app=ondemand */
            applen = 8;
            appnamelen = 8;
        }
        else { /* app!=ondemand, so app is app[/appinstance] */
            if(slash4)
                appnamelen = slash4-p;
            else if(slash3)
                appnamelen = slash3-p;
            else if(slash2)
                appnamelen = slash2-p;

            applen = appnamelen;
        }

        app->av_val = p;
        app->av_len = applen;

        LOGI("RTMP_ParseURL","Parsed app     : %.*s", applen, p);

        p += appnamelen;
    }

    if (*p == '/')
        p++;

    if (end-p) {
        AVal av = {p, static_cast<int>(end - p)};
       // RTMP_ParsePlaypath(&av, playpath); // DON'T THINK i NEED THIS RIGHT NOW
        LOGI("RTMP_ParseURL","RTMP_ParsePlaypath(&av, playpath)");
    }
    LOGI("RTMP_ParseURL","NO --->  RTMP_ParsePlaypath");

    return RTMP_SUCCESS;
}
