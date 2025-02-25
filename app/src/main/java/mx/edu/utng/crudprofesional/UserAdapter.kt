package mx.edu.utng.crudprofesional

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter(
    private var users: List<User>,
    private val dbHelper: DatabaseHelper,
    private val listener: OnUserActionListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    fun updateList(newList: List<User>) {
        users = newList
        notifyDataSetChanged()
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgProfile: ImageView = view.findViewById(R.id.imgProfile)
        private val txtName: TextView = view.findViewById(R.id.txtName)
        private val txtEmail: TextView = view.findViewById(R.id.txtEmail)
        private val btnEdit: Button = view.findViewById(R.id.btnEdit)
        private val btnDelete: Button = view.findViewById(R.id.btnDelete)

        fun bind(user: User) {
            txtName.text = user.name
            txtEmail.text = user.email
            Glide.with(itemView.context).load(user.imageUri).into(imgProfile)

            btnEdit.setOnClickListener {
                val intent = Intent(itemView.context, EditUserActivity::class.java)
                intent.putExtra("USER_ID", user.id)
                itemView.context.startActivity(intent)
            }

            btnDelete.setOnClickListener {
                dbHelper.deleteUser(user.id)
                listener.onUserDeleted()
            }
        }
    }
}

interface OnUserActionListener {
    fun onUserDeleted()
}
