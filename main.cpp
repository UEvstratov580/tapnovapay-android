#include <QApplication>
#include <QPushButton>
#include <QMessageBox>

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    
    QPushButton button("TapNovaPay");
    button.resize(300, 100);
    QObject::connect(&button, &QPushButton::clicked, []() {
        QMessageBox::information(nullptr, "Info", "TapNovaPay Wallet");
    });
    button.show();
    
    return app.exec();
}
