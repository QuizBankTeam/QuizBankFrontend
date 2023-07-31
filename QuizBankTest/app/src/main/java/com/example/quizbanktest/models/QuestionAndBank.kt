import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.models.QuestionSetModel
import java.io.Serializable

data class QuestionAndBank(
    val _id: String,
    val title: String,
    val questionBankType: String,
    val createdDate: String,
    val members : ArrayList<String>,
    val originateFrom : String,
    val creator:String,
    val questionSets:ArrayList<QuestionSetModel>,
    val questions:ArrayList<QuestionModel>
): Serializable