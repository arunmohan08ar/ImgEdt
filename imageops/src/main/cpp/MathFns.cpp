#include "cmath"


float gsian(int frm,int to,int mid,int x,int intensity){
    return exp((-intensity+1)*((x-mid)*(x-mid))/(((to-frm)*(to-frm))+.0001f));
}


inline void set_gsian(int frm,int to,int rm,int gm,int bm,int& r,int& g,int& b,float& rtr,float& rtg,float&rtb){
    float cnst=(to-frm)/(-5.0f);
    rtr= exp(cnst*((r-rm)*(r-rm))/((to-frm)*(to-frm)));
    rtg= exp(cnst*((g-gm)*(g-gm))/((to-frm)*(to-frm)));
    rtb= exp(cnst*((b-bm)*(b-bm))/((to-frm)*(to-frm)));
}


inline void set_gsian(int frm,int to,int& r,int& g,int& b,float& rtr,float& rtg,float&rtb){
    float cnst=(to-frm)/(-5.0f);
    int mvl=(to+frm)/2;
    rtr= exp(cnst*((float)(r-mvl)*(r-mvl))/((to-frm)*(to-frm)));
    rtg= exp(cnst*((float)(g-mvl)*(g-mvl))/((to-frm)*(to-frm)));
    rtb= exp(cnst*((float)(b-mvl)*(b-mvl))/((to-frm)*(to-frm)));
}