#include <jni.h>
#include "humanDetection.h"
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect.hpp>
#include <opencv2/imgproc.hpp>

using namespace std;
using namespace cv;


extern "C"
JNIEXPORT void JNICALL
Java_com_android_arengiene_ArEngineController_00024ArEngineControllerImpl_humanDetection(
        JNIEnv *env, jobject thiz, jlong matAddr) {

    Mat img = *(Mat *) matAddr;
    Mat gray;
    cv::cvtColor(img, gray, cv::COLOR_BGR2GRAY);

    HOGDescriptor hog;
    hog.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());

    vector<Rect> found, foundFiltered;
    hog.detectMultiScale(gray, found, 0, Size(8, 8), Size(32, 32), 1.05, 2);

    size_t i, j;
    for (i = 0; i < found.size(); i++) {
        Rect r = found[i];
        for (j = 0; j < found.size(); j++)
            if (j != i && (r & found[j]) == r)
                break;
        if (j == found.size())
            foundFiltered.push_back(r);
    }

    if (foundFiltered.empty()) return;
    Rect r = foundFiltered[0];
    rectangle(img, r.tl(), r.br(), Scalar(255, 0, 0));
}