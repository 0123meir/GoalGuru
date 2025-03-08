package com.example.goalguru.models

class Model private constructor() {

    val tasks: MutableList<Task> = ArrayList()

    companion object {
        val shared = Model()
    }

    init {
        tasks.add(Task(
            "Save for Vacation",
            "Set up a dedicated savings account - I'm making this one extra long to see how it handles text wrapping",
            deadlineTemplate(5),
            false))
        tasks.add(Task("Save for Vacation", "Cut dining out expenses by 50%", deadlineTemplate(7), false))
        tasks.add(Task("Save for Vacation", "Research budget travel options", deadlineTemplate(10), false))
        tasks.add(Task("Save for Vacation", "Save $200 from each paycheck", deadlineTemplate(15), true))
        tasks.add(Task("Run a 5K", "Buy running shoes", deadlineTemplate(3), true))
        tasks.add(Task("Run a 5K", "Create a training schedule", deadlineTemplate(5), false))
        tasks.add(Task("Run a 5K", "Run 1K without stopping", deadlineTemplate(10), false))
        tasks.add(Task("Run a 5K", "Increase distance to 3K", deadlineTemplate(20), false))
        tasks.add(Task("Run a 5K", "Sign up for a local 5K race", deadlineTemplate(25), false))
    }

    private fun deadlineTemplate(days: Int): String {
        return "Deadline: in $days days"
    }
}