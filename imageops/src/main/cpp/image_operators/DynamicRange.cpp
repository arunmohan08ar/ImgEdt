//
// Created by arnb on 28/06/21.
//
#include <android/bitmap.h>
#include "../Bitmap_Utils.h"

/*#include <android/log.h>

#define  LOG_TAG    "cpp_test"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
*/


template <typename Int,typename UInt32P,typename ABmpInf>
static void  dynamic_Range( Int hFrm,
                     Int hTo,
                     Int frm,
                     Int to,
                     UInt32P line_bmp,
                     ABmpInf info_bmp
){
    const int wBmp  = info_bmp->width,
              hBmp  = info_bmp->height;
    const long bsze=wBmp*hBmp;

    long rPos,bPos,bEnd,bI,px;
    int a,r,g,b;
    int start=maxfn(0,frm);
    int upto=minfn(255,to);
    const float diff=(upto-start)<1?1.0f:(float)(upto-start);
    float rto=255.0f/diff;

    for(int i=hFrm;i<hTo;i++){
        bPos=i*wBmp;
        bEnd=bPos+wBmp;
        if(rPos<0) continue;
        if(bEnd>=bsze){
            break;
        }
        for(int j=0;j<wBmp;j++){
            bI=j+bPos;
            px=line_bmp[bI];
            gt_RGBA(px,r,g,b,a);
            if(!a){
                continue;
            }
            r-=frm;
            g-=frm;
            b-=frm;
            r*=rto;
            g*=rto;
            b*=rto;

            if(r<0)r=0;
            else if(r>255) r=255;
            if(g<0)g=0;
            else if(g>255) g=255;
            if(b<0)b=0;
            else if(b>255) b=255;

            line_bmp[bI]=get_Pixel(r,g,b,a);
        }
    }
}

