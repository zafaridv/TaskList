package com.mzafari.tasklist.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * uitility class with including helper methods
 */
class Utils {
    companion object{

        /**
         * convert date object to string and return date string with specified date format
         */
        fun dateToString(date: Date,format:String="yyyy-MM-dd"):String{
            val sdf = SimpleDateFormat(format)
            return sdf.format(date)
        }
    }
}