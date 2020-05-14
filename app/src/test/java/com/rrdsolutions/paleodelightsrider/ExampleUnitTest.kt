package com.rrdsolutions.paleodelightsrider

import android.app.Instrumentation
import android.content.pm.InstrumentationInfo
import com.google.api.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Test

import org.junit.Assert.*
import java.lang.StrictMath.abs
import java.security.AccessController.getContext

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun division(){
        assertEquals(10, (60-40)/2)
    }
    @Test fun difference(){
        assertEquals(2, abs(2-4))
    }

    @Test fun query(){
        //FirebaseApp.initializeApp(Instrumentation.ActivityMonitor().)
        fun queryDB(callback: (String) -> Unit){

            val db = FirebaseFirestore.getInstance()
                .collection("Collection").document("Document")

            db.get()
                .addOnSuccessListener{
                    val result = it.data?.get("Name") as String
                    callback(result)
                }
        }
        queryDB{
            val name = it

            assertEquals("David", name)
        }

    }
}