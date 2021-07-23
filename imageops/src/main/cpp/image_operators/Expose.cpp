#include <android/bitmap.h>
#include "vector"
#include "future"
#include "../MathFns.h"
#include "../Bitmap_Utils.h"

/*
#include <android/log.h>
#define  LOG_TAG    "cpp_test"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
*/

template <typename Int,typename UInt32P,typename ABmpInf>
static void expose(Int hFrom,
                   Int hTo,
                   Int frm,
                   Int to,
                   Int rX,
                   Int rY,
                   Int sX,
                   Int sY,
                   Int intensity,
                   UInt32P line_bmp,
                   UInt32P line_ref,
                   UInt32P line_sec,
                   ABmpInf info_bmp,
                   ABmpInf info_ref,
                   ABmpInf info_sec
){
    const int   wBmp  = info_bmp->width,
                hBmp  = info_bmp->height,
                wRef  = info_ref->width,
                hRef  = info_ref->height,
                wSec  = info_sec->width,
                hSec  = info_sec->height,
                mid   = (frm+to)/2;


    const long  rSze=wRef*hRef,
                bSze=wBmp*hBmp,
                sSze=wSec*hSec;

    long rPos,bPos,sPos,rEnd,bEnd,sEnd,bI,rI,sI,px,pxs;
    int a,r,g,b,mean,rs,gs,bs,as;
    float rto,
          intense=getIntensityNormal(intensity)+1.0f;
    bool changeFlag=false;
    for(long i=hFrom;i<hTo;i++){
        rPos=(i+rY)*wRef;
        if(rPos<0) continue;
        sPos=(i+sY)*wSec;
        if(sPos<0) continue;

        rEnd=rPos+wRef;
        sEnd=sPos+wSec;
        bPos=i*wBmp;
        bEnd=bPos+wBmp;
        if(bEnd>=bSze || rEnd>=rSze || sEnd>=sSze){
            break;
        }
        for(int j=0;j<wBmp;j++){
            rI=j+rPos+rX;
            if(rI<rPos || rI<0)     continue;
            if(rI>=rEnd)            break;

            sI=j+sPos+sX;
            if(sI<sPos || sI<0)     continue;
            if(sI>=sEnd)            break;

            bI=j+bPos;

            px=line_ref[rI];
            gt_RGBA(px,r,g,b,a);
            if(!a){
                continue;
            }
            pxs=line_sec[sI];
            gt_RGBA(pxs,rs,gs,bs,as);
            if(!as){
                continue;
            }
            mean=getMeanWRA(r,g,b,a);
            if(frm<=to) {
                if (mean<= frm) {
                    rto = ((frm - mean)) / 255.0f;
                    changeFlag=true;
                } else if (mean > to) {
                    rto = ((mean) -to) / 255.0f;
                    changeFlag=true;
                }
                rto*=intense;
                if(rto<0){
                    rto=0;
                }else if(rto>1){
                    rto=1;
                }
            }else{
                rto= gsian(frm,to,mid,mean,intense);
                changeFlag=true;
            }
            if(changeFlag){
                rto*=((float)as/255);
                px=line_bmp[bI];
                gt_RGBA(px,r,g,b,a);
                r=(int)(((float)r*(1-rto)) + ((float)rs*(rto)));
                g=(int)(((float)g*(1-rto)) + ((float)gs*(rto)));
                b=(int)(((float)b*(1-rto)) + ((float)bs*(rto)));
                a=(int)(((float)a*(1-rto)) + ((float)as*(rto)));
                line_bmp[bI]= get_Pixel(r,g,b,a);
                changeFlag=false;
            }
        }
    }
}









/*


template <typename Int,typename UInt32P,typename ABmpInf>
static void exposeTmp(Int bmpHFrm,
                   Int bmpHTo,
                   Int bmpWFrm,
                   Int bmpWTo,
                   Int frm,
                   Int to,
                   Int rX,
                   Int rY,
                   Int sX,
                   Int sY,
                   Int intense,
                   UInt32P line_bmp,
                   UInt32P line_ref,
                   UInt32P line_exp,
                   ABmpInf info_bmp,
                   ABmpInf info_ref,
                   ABmpInf info_expsd
            ){
    const unsigned long exp_sze=info_expsd->width*info_expsd->height;
    const unsigned long ref_sze=info_ref->width*info_ref->height;
    const long expW=info_expsd->width, refW=info_ref->width;

    long px,pxe,pos,lne_strt,sXp,sYp,rXp,rYp;
    int mean,r,g,b,a,re,ge,be,ae;
    float rto;

    int mid=(frm+to)/2;
    bool changeFlag=false;
    float intensity=(intense/100.0f)+1.0f;
    for(long i=bmpHFrm;i<bmpHTo;i++){
        pos=bmpWFrm-1;
        lne_strt=(i*info_bmp->width) ;
        sYp=((i+sY)*expW);
        rYp=((i+rY)*refW);
        while(++pos<bmpWTo){
            sXp=pos+sX;
            rXp=pos+rX;
            if(sXp >= expW || rXp>=refW) {
                break;
            }else if(sXp<0 || rXp<0){
                continue;
            }
            if((sYp+sXp)>=exp_sze || (rYp+rXp)>=ref_sze) {
                break;
            }else if((sYp+sXp)<0 || (rYp+rXp)<0){
                continue;
            }
            px=line_ref[rYp+rXp];
            gt_RGBA(px,r,g,b,a);
            pxe=line_exp[sYp+sXp];
            gt_RGBA(pxe,re,ge,be,ae);
            mean=(r+g+b)/3;
            if(frm<=to) {
                if (mean<= frm) {
                    rto = ((frm - mean)) / 255.0f;
                    changeFlag=true;
                } else if (mean > to) {
                    rto = ((mean) -to) / 255.0f;
                    changeFlag=true;
                }
                rto*=intensity;
                if(rto<0){
                    rto=0;
                }else if(rto>1){
                    rto=1;
                }
            }else{
                rto=gsian(frm,to,mid,mean,intensity);
                changeFlag=true;
            }
            if(changeFlag){
                rto*=((float)ae/255);
                px=line_bmp[lne_strt+pos];
                gt_RGBA(px,r,g,b,a);
                r=(int)((r*(1-rto)) + (re*(rto)));
                g=(int)((g*(1-rto)) + (ge*(rto)));
                b=(int)((b*(1-rto)) + (be*(rto)));
                a=(int)((a*(1-rto)) + (ae*(rto)));
                line_bmp[lne_strt+pos]= gt_Pxl(r,g,b,a);
                changeFlag=false;
            }
        }
    }
}


static void  exposeImage(uint32_t* line_bmp,
                         uint32_t* line_ref,
                         uint32_t* line_exp,
                         AndroidBitmapInfo& info_bmp,
                         AndroidBitmapInfo& info_ref,
                         AndroidBitmapInfo& info_expsd,
                         int frm,
                         int to,
                         int intensity,
                         int rX,
                         int rY,
                         int sX,
                         int sY
){
    //LOGI("in exp");
    const int spltH=2,spltW=3;
    const int bmpW=info_bmp.width;
    const int bmpH=info_bmp.height;
    const int bmpWF=info_bmp.width/spltW;
    const int bmpHF=info_bmp.height/spltH;
    std::vector<std::future<void>> asyncs;
  //  LOGI("b4 async loop exp");

    for(int h=0;h<spltH;h++){
        for(int w=0;w<spltW;w++){
            int htr=bmpHF*(h+1);
            int wtr=bmpWF*(w+1);
            htr=bmpH<htr?bmpH:htr;
            wtr=bmpW<wtr?bmpW:wtr;
            int fh=(h*bmpHF);
            int fw=(w*bmpWF);
            asyncs.push_back(std::async(
                    std::launch::async,
                    exposeTmp<int,uint32_t*,AndroidBitmapInfo*>,
                    fh,
                    htr,
                    fw,
                    wtr,
                    frm,
                    to,
                    rX,
                    rY,
                    sX,
                    sY,
                    intensity,
                    line_bmp,
                    line_ref,
                    line_exp,
                    &info_bmp,
                    &info_ref,
                    &info_expsd
                             )
            );
        }
    }
    for(auto& m:asyncs){
        m.wait();
    }
}
*/
