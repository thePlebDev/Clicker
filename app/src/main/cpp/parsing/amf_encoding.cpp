//
// Created by Tristan on 2025-02-12.
//

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../rtmp_client.h"
#include <android/log.h>


#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))

char* AMF_EncodeNumber(char *output, char *outend, double dVal){
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

char* AMF_EncodeInt16(char *output, char *outend, short nVal){
    if (output+2 > outend)
        return NULL;

    output[1] = nVal & 0xff;
    output[0] = nVal >> 8;
    return output+2;
}
char* AMF_EncodeInt32(char *output, char *outend, int nVal){
    if (output+4 > outend)
        return NULL;

    output[3] = nVal & 0xff;
    output[2] = nVal >> 8;
    output[1] = nVal >> 16;
    output[0] = nVal >> 24;
    return output+4;
}
char * AMF_EncodeString(char *output, char *outend, const AVal *bv){
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

char* AMF_EncodeNamedString(char *output, char *outend, const AVal *strName, const AVal *strValue){
    if (output+2+strName->av_len > outend)
        return NULL;
    output = AMF_EncodeInt16(output, outend, strName->av_len);

    memcpy(output, strName->av_val, strName->av_len);
    output += strName->av_len;

    return AMF_EncodeString(output, outend, strValue);
}
char* AMF_EncodeBoolean(char *output, char *outend, int bVal)
{
    if (output+2 > outend)
        return NULL;

    *output++ = AMF_BOOLEAN;

    *output++ = bVal ? 0x01 : 0x00;

    return output;
}

char* AMF_EncodeNamedBoolean(char *output, char *outend, const AVal *strName, int bVal)
{
    if (output+2+strName->av_len > outend)
        return NULL;
    output = AMF_EncodeInt16(output, outend, strName->av_len);

    memcpy(output, strName->av_val, strName->av_len);
    output += strName->av_len;

    return AMF_EncodeBoolean(output, outend, bVal);
}
char* AMF_EncodeNamedNumber(char *output, char *outend, const AVal *strName, double dVal)
{
    if (output+2+strName->av_len > outend)
        return NULL;
    output = AMF_EncodeInt16(output, outend, strName->av_len);

    memcpy(output, strName->av_val, strName->av_len);
    output += strName->av_len;

    return AMF_EncodeNumber(output, outend, dVal);
}

char* AMF_EncodeInt24(char *output, char *outend, int nVal){
    if (output+3 > outend)
        return NULL;

    output[2] = nVal & 0xff;
    output[1] = nVal >> 8;
    output[0] = nVal >> 16;
    return output+3;
}

/* AMFObject */

char *
AMF_Encode(AMFObject *obj, char *pBuffer, char *pBufEnd)
{
    int i;

    if (pBuffer+4 >= pBufEnd)
        return NULL;

    *pBuffer++ = AMF_OBJECT;

    for (i = 0; i < obj->o_num; i++)
    {
        char *res = AMFProp_Encode(&obj->o_props[i], pBuffer, pBufEnd);
        if (res == NULL)
        {

            LOGI("AMF_Encode", "AMF_Encode - failed to encode property in index %d",i);
            break;
        }
        else
        {
            pBuffer = res;
        }
    }

    if (pBuffer + 3 >= pBufEnd)
        return NULL;			/* no room for the end marker */

    pBuffer = AMF_EncodeInt24(pBuffer, pBufEnd, AMF_OBJECT_END);

    return pBuffer;
}
char* AMF_EncodeEcmaArray(AMFObject *obj, char *pBuffer, char *pBufEnd){
    int i;

    if (pBuffer+4 >= pBufEnd)
        return NULL;

    *pBuffer++ = AMF_ECMA_ARRAY;

    pBuffer = AMF_EncodeInt32(pBuffer, pBufEnd, obj->o_num);

    for (i = 0; i < obj->o_num; i++)
    {
        char *res = AMFProp_Encode(&obj->o_props[i], pBuffer, pBufEnd);
        if (res == NULL){

            LOGI("AMFProp_Encode",  "AMF_Encode - failed to encode property in index %d",i);
            break;
        }
        else
        {
            pBuffer = res;
        }
    }

    if (pBuffer + 3 >= pBufEnd)
        return NULL;			/* no room for the end marker */

    pBuffer = AMF_EncodeInt24(pBuffer, pBufEnd, AMF_OBJECT_END);

    return pBuffer;
}

char * AMF_EncodeArray(AMFObject *obj, char *pBuffer, char *pBufEnd){
    int i;

    if (pBuffer+4 >= pBufEnd)
        return NULL;

    *pBuffer++ = AMF_STRICT_ARRAY;

    pBuffer = AMF_EncodeInt32(pBuffer, pBufEnd, obj->o_num);

    for (i = 0; i < obj->o_num; i++)
    {
        char *res = AMFProp_Encode(&obj->o_props[i], pBuffer, pBufEnd);
        if (res == NULL)
        {

            LOGI("AMF_EncodeArray",  "AMF_Encode - failed to encode property in index %d");
            break;
        }
        else
        {
            pBuffer = res;
        }
    }

    if (pBuffer + 3 >= pBufEnd)
      return NULL;			/* no room for the end marker */

    pBuffer = AMF_EncodeInt24(pBuffer, pBufEnd, AMF_OBJECT_END);

    return pBuffer;
}

char* AMFProp_Encode(AMFObjectProperty *prop, char *pBuffer, char *pBufEnd){
    if (prop->p_type == AMF_INVALID)
        return NULL;

    if (prop->p_type != AMF_NULL && pBuffer + prop->p_name.av_len + 2 + 1 >= pBufEnd)
        return NULL;

    if (prop->p_type != AMF_NULL && prop->p_name.av_len)
    {
        *pBuffer++ = prop->p_name.av_len >> 8;
        *pBuffer++ = prop->p_name.av_len & 0xff;
        memcpy(pBuffer, prop->p_name.av_val, prop->p_name.av_len);
        pBuffer += prop->p_name.av_len;
    }

    switch (prop->p_type)
    {
        case AMF_NUMBER:
            pBuffer = AMF_EncodeNumber(pBuffer, pBufEnd, prop->p_vu.p_number);
            break;

        case AMF_BOOLEAN:
            pBuffer = AMF_EncodeBoolean(pBuffer, pBufEnd, prop->p_vu.p_number != 0);
            break;

        case AMF_STRING:
            pBuffer = AMF_EncodeString(pBuffer, pBufEnd, &prop->p_vu.p_aval);
            break;

        case AMF_NULL:
            if (pBuffer+1 >= pBufEnd)
                return NULL;
            *pBuffer++ = AMF_NULL;
            break;

        case AMF_OBJECT:
            pBuffer = AMF_Encode(&prop->p_vu.p_object, pBuffer, pBufEnd);
            break;

        case AMF_ECMA_ARRAY:
            pBuffer = AMF_EncodeEcmaArray(&prop->p_vu.p_object, pBuffer, pBufEnd);
            break;

        case AMF_STRICT_ARRAY:
            pBuffer = AMF_EncodeArray(&prop->p_vu.p_object, pBuffer, pBufEnd);
            break;

        default:

            LOGI("AMFProp_Encode",  "%s, invalid type. %d", __FUNCTION__, prop->p_type);
            pBuffer = NULL;
    };

    return pBuffer;
}