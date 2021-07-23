#include <android/bitmap.h>
#include "vector"
#include "future"

#include "../ImageOperators.h"


static void  startAsync(Operators op,
                         uint32_t* line_bmp,
                         uint32_t* line_ref,
                         uint32_t* line_sec,
                         AndroidBitmapInfo& info_bmp,
                         AndroidBitmapInfo& info_ref,
                         AndroidBitmapInfo& info_sec,
                         int frm,
                         int to,
                         int intensity,
                         int rX,
                         int rY,
                         int sX,
                         int sY,
                         const int HEIGHT_SPLIT
){
    const int bmpH=info_bmp.height;

    std::vector<std::future<void>> asyncs;
    std::future<void> f;
    int hFrom,hTo;
    int hFactor=bmpH/HEIGHT_SPLIT;
    for(int i=0;i<HEIGHT_SPLIT;i++){
        hFrom=i*hFactor;
        hTo=hFrom+hFactor;
        switch(op){
            case Operators::BINARY:{
                asyncs.push_back(std::async(
                        std::launch::async,
                        binary<int,uint32_t*,AndroidBitmapInfo*>,
                        hFrom,
                        hTo,
                        frm,
                        to,
                        rX,
                        rY,
                        line_bmp,
                        line_ref,
                        &info_bmp,
                        &info_ref
                        )
                );
                break;
            }case Operators::INVERSE:{
                asyncs.push_back(std::async(
                        std::launch::async,
                        inverse<int,uint32_t*,AndroidBitmapInfo*>,
                        hFrom,
                        hTo,
                        frm,
                        to,
                        rX,
                        rY,
                        line_bmp,
                        line_ref,
                        &info_bmp,
                        &info_ref
                        )
                );
                break;
            }case Operators::DYNAMIC_RANGE:{
                asyncs.push_back(std::async(
                        std::launch::async,
                        dynamic_Range<int,uint32_t*,AndroidBitmapInfo*>,
                        hFrom,
                        hTo,
                        frm,
                        to,
                        line_bmp,
                        &info_bmp
                        )
                );
                break;
            }case Operators::EXPOSE:{
                asyncs.push_back(std::async(
                        std::launch::async,
                        expose<int,uint32_t*,AndroidBitmapInfo*>,
                        hFrom,
                        hTo,
                        frm,
                        to,
                        rX,
                        rY,
                        sX,
                        sY,
                        intensity,
                        line_bmp,
                        line_ref,
                        line_sec,
                        &info_bmp,
                        &info_ref,
                        &info_sec
                    )
                );
                break;
            }
            default:return;
        }

    }
    for(auto& m:asyncs){
        m.wait();
    }
}