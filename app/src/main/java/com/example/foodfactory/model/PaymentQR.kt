package com.example.foodfactory.model

import com.google.type.DateTime

data class PaymentQR (var orderID:String ?= null, var userID:String ?= null, var paymenStatus:Boolean ?= null, var date:DateTime ?= null ){

}