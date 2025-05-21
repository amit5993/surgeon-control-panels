package com.surgeon.controlpanels.model.dbsokect

data class LightDataModel(
    var onOffKey: String,
    var onOffValue: String,
    var intensityKey: String,
    var intensityValue: Int,
    var name: String = "name" // Default name
)