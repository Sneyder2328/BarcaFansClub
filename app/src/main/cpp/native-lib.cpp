#include <jni.h>
#include <string>
#include <iostream>
#include <stdio.h>
#include <string.h>


char* getLiAdy(char* txt);
extern "C"
JNIEXPORT jstring JNICALL Java_com_amazonaws_mobile_AWSConfiguration_getAPI1(JNIEnv *env, jobject instance, jstring s1_, jstring s2_, jstring s3_, jstring s4_, jstring s5_, jstring s6_, jstring s7_, jstring s8_, jstring s9_) {
    const char *s1 = env->GetStringUTFChars(s4_, 0);

    if(s1[0]=='h'){
        const char *txtO = env->GetStringUTFChars(s5_, 0);
        char txt[strlen(txtO)];
        for(int n = 0 ; n <= strlen(txtO) ; n++){
            txt[n] = txtO[n];
        }

        char txt2[80] = "dFcJILtxq";

        char txt3[] = ";zUheqs0Oig.UOs1siOq.;hOs;;eiK110gU;";

        char space[] = " ";
        strcat(txt2, space);
        strcat(txt2, txt3);
        strcat(txt2, space);
        strcat(txt2, txt);

        return env->NewStringUTF(getLiAdy(txt2));
    }
    else if(s1[0]=='y' || s1[0]=='x'){
        char txt[] = "xTOLHT7Oh";
        return env->NewStringUTF(getLiAdy(txt));
    }
    else if(s1[0]=='s'){
        char txt[] = "xTOLHT7Oh8hzzUhUUUOUUL3Ob;.iOHLLpOUqs1K1gp;z.U";
        return env->NewStringUTF(getLiAdy(txt));
    }
    else if(s1[0]=='z'){
        char txt[] = "qHR0HpHQT0[xqOxTLRpJ[LTODFcJ[LoxqOeghz11h..";
        return env->NewStringUTF(getLiAdy(txt));
    }\
    /*
    printf(getLiAdy(txt));
    printf("\n");

    char txt2[] = "dFcJILtxq";
    printf(getLiAdy(txt2));
    printf("\n");

    char txt3[] = ";zUheqs0Oig.UOs1siOq.;hOs;;eiK110gU;";
    printf(getLiAdy(txt3));
    printf("\n");

    char txt4[] = "xTOLHT7Oh";
    printf(getLiAdy(txt4));
    printf("\n");

    char txt5[] = "xTOLHT7Oh8hzzUhUUUOUUL3Ob;.iOHLLpOUqs1K1gp;z.U";
    printf(getLiAdy(txt5));
    printf("\n");

    char txt6[] = "qHR0HpHQT0[xqOxTLRpJ[LTODFcJ[LoxqOeghz11h..";
    printf(getLiAdy(txt6));

    env->ReleaseStringUTFChars(s1_, s1);
    env->ReleaseStringUTFChars(s2_, s2);

    return env->NewStringUTF(s1);*/
}

char* getLiAdy(char* txt){
    for(int n = 0 ; n < strlen(txt) ; n++){
        if(txt[n]=='H'){
            txt[n] = 'a';
        }
        else if(txt[n]=='G'){
            txt[n] = 'w';
        }
        else if(txt[n]=='T'){
            txt[n] = 's';
        }
        else if(txt[n]=='O'){
            txt[n] = '-';
        }
        else if(txt[n]=='D'){
            txt[n] = 'm';
        }
        else if(txt[n]=='S'){
            txt[n] = 'y';
        }
        else if(txt[n]=='W'){
            txt[n] = 'p';
        }
        else if(txt[n]=='I'){
            txt[n] = 'l';
        }
        else if(txt[n]=='L'){
            txt[n] = 'e';
        }
        else if(txt[n]=='H'){
            txt[n] = 'a';
        }
        else if(txt[n]=='V'){
            txt[n] = 'p';
        }
        else if(txt[n]=='R'){
            txt[n] = 'r';
        }
        else if(txt[n]=='F'){
            txt[n] = 'o';
        }
        else if(txt[n]=='J'){
            txt[n] = 'i';
        }
        else if(txt[n]=='Z'){
            txt[n] = 'v';
        }
        else if(txt[n]=='Q'){
            txt[n] = 'n';
        }
        else if(txt[n]=='K'){
            txt[n] = 'd';
        }
        else if(txt[n]=='f'){
            txt[n] = '.';
        }
        else if(txt[n]=='U'){
            txt[n] = '0';
        }
        else if(txt[n]=='h'){
            txt[n] = '1';
        }
        else if(txt[n]=='e'){
            txt[n] = '3';
        }
        else if(txt[n]=='d'){
            txt[n] = 'M';
        }
        else if(txt[n]=='x'){
            txt[n] = 'u';
        }
        else if(txt[n]=='q'){
            txt[n] = 'b';
        }
        else if(txt[n]=='t'){
            txt[n] = 'H';
        }
        else if(txt[n]=='I'){
            txt[n] = 'l';
        }
        else if(txt[n]=='c'){
            txt[n] = 'b';
        }
        else if(txt[n]=='7'){
            txt[n] = 't';
        }
        else if(txt[n]=='0'){
            txt[n] = 'c';
        }
        else if(txt[n]=='.'){
            txt[n] = '6';
        }
        else if(txt[n]=='z'){
            txt[n] = '2';
        }
        else if(txt[n]==';'){
            txt[n] = '7';
        }
        else if(txt[n]=='['){
            txt[n] = 'l';
        }
        else if(txt[n]=='p'){
            txt[n] = 'f';
        }
        else if(txt[n]=='g'){
            txt[n] = '9';
        }
        else if(txt[n]=='o'){
            txt[n] = 'h';
        }
        else if(txt[n]=='L'){
            txt[n] = 'e';
        }
        else if(txt[n]=='H'){
            txt[n] = 'a';
        }
        else if(txt[n]=='b'){
            txt[n] = '4';
        }
        else if(txt[n]=='1'){
            txt[n] = '5';
        }
        else if(txt[n]=='3'){
            txt[n] = '5';
        }
        else if(txt[n]=='i'){
            txt[n] = '8';
        }
        else if(txt[n]=='s'){
            txt[n] = '4';
        }
        else if(txt[n]=='8'){
            txt[n] = ':';
        }
    }
    //printf(txt);
    return txt;
}
