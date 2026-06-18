QT += core widgets network
CONFIG += c++17

TARGET = TapNovaPay
TEMPLATE = app

SOURCES += main.cpp
HEADERS += 
FORMS += 

android {
    QT += androidextras
    ANDROID_PACKAGE_SOURCE_DIR = $$PWD/android
    ANDROID_PERMISSIONS += android.permission.INTERNET
}

LIBS += -lssl -lcrypto -lcurl -lsqlite3

DEFINES += QT_DEPRECATED_WARNINGS
