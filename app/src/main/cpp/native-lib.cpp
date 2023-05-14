#include <jni.h>
#include "humanDetection.h"
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect.hpp>
#include <opencv2/imgproc.hpp>

using namespace std;
using namespace cv;


extern "C"
JNIEXPORT jobject JNICALL
Java_com_android_arengiene_HumanDetector_00024HumanDetectorImpl_humanDetection(
        JNIEnv *env, jobject thiz, jlong matAddr) {

    Mat img = *(Mat *) matAddr;
    Mat gray;
    cv::cvtColor(img, gray, cv::COLOR_BGR2GRAY);

    HOGDescriptor hog;
    hog.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());

    vector<Rect> found, foundFiltered;
    hog.detectMultiScale(gray, found, 0, Size(8, 8), Size(32, 32), 1.05, 2);

    Mat depth = *(Mat *) depthAddr;
    vector<Mat> channels;
    split(depth, channels);

    size_t i, j;
    for (i = 0; i < found.size(); i++) {
        Rect r = found[i];
        for (j = 0; j < found.size(); j++)
            if (j != i && (r & found[j]) == r)
                break;
        if (j == found.size())
            foundFiltered.push_back(r);
    }

    if (foundFiltered.empty()) return nullptr;

    jclass pointClass = env->FindClass("org/opencv/core/Point");
    jmethodID pointConstructor = env->GetMethodID(pointClass, "<init>", "(DD)V");

    Rect r = foundFiltered[0];
    jobject point1 = env->NewObject(pointClass, pointConstructor, (double) r.tl().x,(double) r.tl().y);
    jobject point2 = env->NewObject(pointClass, pointConstructor, (double) r.br().x, (double)r.br().y);
    jclass rectClass = env->FindClass("org/opencv/core/Rect");
    jmethodID rectConstructor = env->GetMethodID(rectClass, "<init>", "(Lorg/opencv/core/Point;Lorg/opencv/core/Point;)V");
    jobject rect = env->NewObject(rectClass, rectConstructor, point1, point2);
    return rect;
}