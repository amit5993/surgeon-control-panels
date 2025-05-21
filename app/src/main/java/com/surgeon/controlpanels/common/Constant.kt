package com.surgeon.controlpanels.common

class Constant {

    companion object {
        const val BASE_URL = "http://dcp.server.wiespl.com/"
        const val CCTV_BASE_URL = "http://192.168.1.39:5000/"

        //        const val DECOM_URL = "https://dicom.server.wiespl.com/tests/"
        const val DECOM_URL = "https://dicom.server.wiespl.com/tests/pacs/index.html"
        const val SOCKET_URL = "ws://216.10.243.73/dcp/ws/" //live

        //        const val SOCKET_URL = "ws://192.168.0.219:8085/dcp/ws/"
//        const val IMAGE_BASE_URL = "${BASE_URL}uploads/patients/"
        const val IMAGE_BASE_URL = "${BASE_URL}uploads/products/"

        const val LOGIN = "${BASE_URL}api/common/login"
        const val LOGOUT = "${BASE_URL}api/common/logout/tablet"
        const val CCTV_LIST = "${BASE_URL}api/cctv-list/"
        const val CATEGORY_LIST = "${BASE_URL}api/category-list"
        const val STORE_LIST = "${BASE_URL}api/store-list"
        const val PRODUCT_LIST = "${BASE_URL}api/product-list"
        const val UPCOMING_SURGERY = "${BASE_URL}api/upcoming-surgery/"
        const val CHECK_LIST = "${BASE_URL}api/get-checklist/"
        const val LATEST_SURGERY = "${BASE_URL}api/latest-surgery/"
        const val GET_PARAMETER = "${BASE_URL}api/get-parameters/"
        const val GENERAL_REQUEST = "${BASE_URL}api/general-request"
        const val STORE_CLEAN = "${BASE_URL}api/store-cleaning-activity"
        const val STORE_CLEAN_BULK = "${BASE_URL}api/store-cleaning-activity-bulk"
        const val UPLOAD_CLEANING_PHOTOS = "${BASE_URL}api/upload-cleaning-photos"
        const val TEMP_HUMIDITY_REQUEST = "${BASE_URL}api/temperature-humidity/request"
        const val EXTENSIONS = "${BASE_URL}api/extensions"
        const val PATIENT_SURGERY_LIST = "${BASE_URL}api/patient-surgery-list/"
        const val CLEANING_PENDING = "${BASE_URL}api/patient-surgery/cleaning-pending/"
        const val UPDATE_CLEANING_STATUS = "${BASE_URL}api/update-cleaning-status"
        const val UPDATE_OT_MODE = "${BASE_URL}api/update-ot-mode"
        const val GET_OT_MODE = "${BASE_URL}api/ot-details/"
        const val UPDATE_SURGERY_STATUS = "${BASE_URL}api/update-surgery-status"
        const val ORDER_STORE = "${BASE_URL}api/order-store"
        const val ORDER_HISTORY = "${BASE_URL}api/order-list"
        const val POST_API = "${BASE_URL}api/postapi"
        const val GET_DEVICES = "${BASE_URL}api/get-all-devices/"
        const val BROADCAST_CONTACT_LIST = "${BASE_URL}api/broadcast-contact-list"

        const val LIGHT_ON_OFF = "light_on_off"
        const val LIGHT_INTENSITY = "light_intensity"
        const val SENSOR = "sensor"
        const val TEMPERATURE_RH = "temperature_rh"

        const val main = "MAIN"

        const val temp = "TEMP"
        const val hd = "HD"
        const val light = "LIGHT"
        const val alert = "ALERT"
        const val mgps = "MGPS"
        const val timer = "TIMER"
        const val music = "MUSIC"
        const val call = "CALL"
        const val pis = "PIS"

        const val clean = "Clean"
        const val cctv = "CCTV"
        const val entrance = "Entrance"
        const val store = "Store"
        const val menu1 = "menu1"
        const val menu2 = "menu2"
        const val HDMI = "HDMI"
        const val dicom = "Dicom"


        const val degreeSymbol = "\u2103"
        const val percentageSymbol = "%"

        const val PARENT = 0
        const val CHILD = 1
    }

}




