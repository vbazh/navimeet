package com.vbazh.navimeet.data

class Interval {
    var id: Int? = null
    var start: Long? = null
    var end: Long? = null
    var container: String? = null
    var address: String? = null
    var description: String? = null

    constructor() {}

    constructor(
        id: Int?,
        start: Long?,
        end: Long?,
        container: String,
        address: String,
        description: String
    ) {

        this.id = id
        this.start = start
        this.end = end
        this.container = container
        this.address = address
        this.description = description
    }
}