package com.example.myquizapp

object Constants {
    const val USER_NAME: String = "user_name"
    const val TOTAL_QUESTIONS: String = "total_questions"
    const val CORRECT_ANSWERS: String = "correct_answers"
    fun getQuestions():ArrayList<Question>{
        val questionList = ArrayList<Question>()

        val que1 = Question(
            1,"In a hypertonic solution, a bacterial cell will typically?",
            R.drawable.quiz_test,"(A) lyse","(B) burst","(C) stay the same","(D) plasmolyze",1

        )
        questionList.add(que1)

        val que2 = Question(
            2,"由哪一個系統開始,CPU 排程策略成為了作業系統的關鍵要素之一?",
            R.drawable.quiz_test1,"(a)batch","(b)multiprogramming","(c)time sharing","(d)multi-processor",2

        )
        questionList.add(que2)

        val que3 = Question(
            3,"The DNA found in most bacterial cells?",
            R.drawable.quiz_test2,"(A) is surrounded by a nuclear membrane","(B) utilizes histones for chromosomal packaging","(C) is circular in structure","(D) is linear in structure",3

        )
        questionList.add(que3)

        val que4 = Question(
            4,"An experiment began with 4 cells and ended with 128 cells. How many generations\n" +
                    "did the cells go through?",
            R.drawable.quiz_test3,"(A) 64","(B) 32","(C) 6","(D) 5",1

        )
        questionList.add(que4)

        val que5 = Question(
            5,"What is the fate of pyruvic acid in an organism that uses aerobic respiration?",
            R.drawable.quiz_test4,"(A) It is reduced to lactic acid","(B) It reacts with oxaloacetate to form citrate","(C)\n" +
                    "It is oxidized in the electron transport chain","(D) It is converted into acetyl CoA",2

        )
        questionList.add(que5)

        val que6 = Question(
            6,"In bacteria, photosynthetic pigments are found in ?",
            R.drawable.quiz_test5,"(A) chloroplasts","(B) cytoplasm","(C) chromatophores","(D) mesosomes",3

        )
        questionList.add(que6)

        val que7 = Question(
            7,"有關載貨證券在法律上之功能,下列何者有誤?",
            R.drawable.quiz_test,"(A) 貨物收據","(B) 契約證明","(C) 物權證書","(D) 權利擔保",4

        )
        questionList.add(que7)
        return questionList
    }
}