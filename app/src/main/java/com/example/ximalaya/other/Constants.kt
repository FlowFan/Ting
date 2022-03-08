package com.example.ximalaya.other

object Constants {
    const val APP_SECRET = "8646d66d6abe2efd14f2891f9fd1c8af"
    const val APP_KEY = "9f9ef8f10bebeaa83e71e62f935bede8"
    const val PACK_ID = "com.app.test.android"
    const val APP_DATABASE = "app_database"
    const val SHP_DATASTORE = "shp_datastore"
    const val KEY_LAST_OAID = "last_oaid"
    const val KEY_FIRST_START = "first_start"
    const val BASE_URL = "https://api.ximalaya.com/openapi-gateway-app/"

    private const val CLIENT_OS_TYPE = 2
    private const val DEVICE_ID = "ae1416803c3a314d"
    private const val DEVICE_ID_TYPE = "Android_ID"
    private const val DEVICE_TYPE = 2
    private const val SDK_CLIENT_TYPE = 2
    private const val SDK_VERSION = "v8.0.7"

    //获取推荐专辑
    private const val LIKE_COUNT = 10
    const val LIKE_KEY =
        "app_key=$APP_KEY&client_os_type=$CLIENT_OS_TYPE&device_id=$DEVICE_ID&device_id_type=$DEVICE_ID_TYPE&device_type=$DEVICE_TYPE&like_count=$LIKE_COUNT&pack_id=$PACK_ID&sdk_client_type=$SDK_CLIENT_TYPE&sdk_version=$SDK_VERSION"
}