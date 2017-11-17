package com.babychanging.babychanging.ui.upload;

/**
 * Created by vik on 27/10/2017.
 */

public interface UploadView {

    void showPicture();
    void showSourceImageSelection();
    void showRate();
    void showSuccessUpload(String message);
    void showErrorMessage (String message);
    void showAlertDialog(String message);

}
