package mx.edu.utng.crudprofesional

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminActivity : AppCompatActivity(), OnUserActionListener {
    private lateinit var btnViewData: Button
    private lateinit var btnViewUsers: Button
    private lateinit var layoutAdminData: LinearLayout
    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userAdapter: UserAdapter
    private lateinit var btnLogout: ImageButton
    private var userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        btnLogout = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnViewData = findViewById(R.id.btnViewData)
        btnViewUsers = findViewById(R.id.btnViewUsers)
        layoutAdminData = findViewById(R.id.layoutAdminData)
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers)
        searchView = findViewById(R.id.searchView)

        dbHelper = DatabaseHelper(this)

        recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        loadUsers()

        btnViewData.setOnClickListener {
            layoutAdminData.visibility = View.VISIBLE
            recyclerViewUsers.visibility = View.GONE
            searchView.visibility = View.GONE  // Ocultar el SearchView cuando se vea la info del admin
        }

        btnViewUsers.setOnClickListener {
            layoutAdminData.visibility = View.GONE
            recyclerViewUsers.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE  // Hacer visible el SearchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterUsers(newText.orEmpty())
                return true
            }
        })
    }

    private fun loadUsers() {
        userList = dbHelper.getAllUsers().toMutableList()
        userAdapter = UserAdapter(userList, dbHelper, this)
        recyclerViewUsers.adapter = userAdapter
    }

    private fun filterUsers(query: String) {
        val filteredList = userList.filter { user ->
            user.name.contains(query, ignoreCase = true) || user.email.contains(query, ignoreCase = true)
        }
        userAdapter.updateList(filteredList)
    }

    override fun onUserDeleted() {
        loadUsers()
    }
}
