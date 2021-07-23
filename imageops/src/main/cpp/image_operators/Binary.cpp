
#include <android/bitmap.h>
#include "../Bitmap_Utils.h"

/*#include <android/log.h>
#define  LOG_TAG    "cpp_test"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)*/


template <typename Int,typename UInt32P,typename ABmpInf>
static void  binary( Int hFrm,
                     Int hTo,
                     Int frm,
                     Int to,
                     Int rX,
                     Int rY,
                     UInt32P line_bmp,
                     UInt32P line_ref,
                     ABmpInf info_bmp,
                     ABmpInf info_ref
){
    const int wBmp  = info_bmp->width,
              hBmp  = info_bmp->height,
              wRef  = info_ref->width,
              hRef  = info_ref->height;
    const long rsze=wRef*hRef,
            bsze=wBmp*hBmp;

    long rPos,bPos,rEnd,bEnd,bI,rI,px;
    int a,r,g,b,mean;

    for(int i=hFrm;i<hTo;i++){
        rPos=(i+rY)*wRef;
        rEnd=rPos+wRef;
        bPos=i*wBmp;
        bEnd=bPos+wBmp;
        if(rPos<0) continue;
        if(bEnd>=bsze || rEnd>=rsze){
            break;
        }

        for(int j=0;j<wBmp;j++){
            bI=j+bPos;
            rI=j+rPos+rX;
            if(rI<rPos || rI<0) {
                continue;
            }
            if(rI>=rEnd) {
                break;
            }
            px=line_ref[rI];
            gt_RGBA(px,r,g,b,a);
            if(!a){
                continue;
            }
            mean=getMeanWRA(r,g,b,a);
            px=line_bmp[bI];
            gt_Alpha(px,a);

            if(mean<frm){
                line_bmp[bI] = gt_PxlE(0,a);
            }else if(mean>to){
                line_bmp[bI] = gt_PxlE(255,a);
            }
        }
    }
}








/*


static void  binary(uint32_t* line_bmp,
                    uint32_t* line_ref,
                         AndroidBitmapInfo& info_bmp,
                         AndroidBitmapInfo& info_ref,
                         int frm,
                         int to,
                         int intensity,
                         int x,
                         int y
){
    const int wBmp  = info_bmp.width,
              hBmp  = info_bmp.height,
              wRef  = info_ref.width,
              hRef  = info_ref.height;
    const long rsze=wRef*hRef,
               bsze=wBmp*hBmp;

    long rPos,bPos,rEnd,bEnd,bI,rI,px;
    int a,r,g,b,mean;

    for(int i=0;i<hBmp;i++){
        rPos=(i+y)*wRef;
        rEnd=rPos+wRef;
        bPos=i*wBmp;
        bEnd=bPos+wBmp;
        if(rPos<0) continue;
        if(bEnd>=bsze || rEnd>=rsze){
            break;
        }

        for(int j=0;j<wBmp;j++){
            bI=j+bPos;
            rI=j+rPos+x;
            if(rI<rPos || rI<0) {
                continue;
            }
            if(rI>=rEnd) {
                break;
            }
            px=line_ref[rI];
            gt_RGBA(px,r,g,b,a);
            mean=gt_Ref_Bsed_Vl(r,g,b);
            px=line_bmp[bI];
            gt_Alpha(px,a);

            if(mean<frm){
                line_bmp[bI] = gt_Pxl(0,a);
            }else if(mean>to){
                line_bmp[bI] = gt_Pxl(255,a);
            }
        }
    }
}
*/
