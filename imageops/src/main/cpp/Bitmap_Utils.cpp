
static inline int minfn(int a,int b){
    return a<b?a:b;
}
static inline int maxfn(int a,int b){
    return a>b?a:b;
}
static inline void gt_RGBA(long &px,int &r,int &g,int &b,int &a){
    r=(((px) & 0xff));
    g=(((px)>>8) & 0xff);
    b=(((px>>16) & 0xff));
    a=((px>>24) & 0xff);
}
static inline void gt_Alpha(long &px,int &a){
    a=((px>>24) & 0xff);
}

static inline long get_Pixel(int &r,int &g,int &b,int &a){
    return (((a&0xff) << 24 )| ((b&0xff)<< 16) | ((g&0xff)<< 8 ) | r&0xff);
}

static inline long gt_PxlE(int v ,int a){
    return (((a&0xff) << 24 )| ((v&0xff)<< 16) | ((v&0xff)<< 8 ) | v&0xff);
}
static inline int getMeanWRA(int &r,int &g,int &b,int &a){
    return ((r+g+b)/3.0f)*(a/255.0f);
}


static inline float getIntensityNormal(int intense){
    return ((float)intense/100.0f);
}

static inline float pixel_Ratio(unsigned long px){
    return ((float)((((px) & 0xff))+(((px)>>8) & 0xff)+(((px>>16) & 0xff))+((px>>24) & 0xff))/4)/255.0f;
}

