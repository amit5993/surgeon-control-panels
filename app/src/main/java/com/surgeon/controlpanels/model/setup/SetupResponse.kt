package com.surgeon.controlpanels.model.setup

class SetupResponse(
    val id: Int,
    val unique_code: String,
    val operation_theatre_id: Int,
    val device_id: String,
    val app_type: String,
    val status: Int,
    val installation_status: Int,
    val created_by: Int,
    val created_at: String,
    val updated_at: String
)