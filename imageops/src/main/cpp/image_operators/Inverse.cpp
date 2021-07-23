#include <android/bitmap.h>
#include "../Bitmap_Utils.h"

/*#include <android/log.h>

#define  LOG_TAG    "cpp_test"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
*/



template <typename Int,typename UInt32P,typename ABmpInf>
static void  inverse( Int hFrm,
                     Int hTo,
                     Int frm,
                     Int to,
                     Int rX,
                     Int rY,
                     UInt32P line_bmp,
                     UInt32P line_ref,
                     ABmpInf info_bmp,
                     ABmpInf info_ref
) {
    const int wBmp  = info_bmp->width,
              hBmp  = info_bmp->height,
              wRef  = info_ref->width,
              hRef  = info_ref->height;
    const long rsze = wRef*hRef,
               bsze = wBmp*hBmp;

    long rPos,bPos,rEnd,bEnd,bI,rI,px;
    int a,r,g,b,mean;

    for (int i =hFrm; i < hTo; i++) {
        rPos = (i + rY) * wRef;
        rEnd = rPos + wRef;
        bPos = i * wBmp;
        bEnd = bPos + wBmp;
        if (rPos < 0) continue;
        if (bEnd >= bsze || rEnd >= rsze) {
            break;
        }
        for (int j = 0; j < wBmp; j++) {
            bI = j + bPos;
            rI = j + rPos + rX;
            if (rI < rPos || rI < 0) {
                continue;
            }
            if (rI >= rEnd) {
                break;
            }
            px = line_ref[rI];
            gt_RGBA(px, r, g, b, a);
            if(!a){
                continue;
            }
            mean = getMeanWRA(r, g, b,a);
            px = line_bmp[bI];
            gt_RGBA(px, r, g, b, a);

            if (mean < frm || mean > to) {
                r = 255 - r;
                g = 255 - g;
                b = 255 - b;
            }else {
                continue;
            }

            line_bmp[bI] = get_Pixel(r, g, b, a);
        }
    }
}