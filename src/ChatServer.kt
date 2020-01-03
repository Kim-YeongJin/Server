import java.net.SocketAddress

fun main() {
    ChatServer()
}

class ChatServer : MacheteServer(200, "192.168.1.105", 3000 , 10240 , 10240, "jdbc:mariadb://127.0.0.1:3306/chat", "root", "0523") {
    override fun qaddRoom() {
        qadd("chat log", "insert ch_log_chat (ch_logc_user, ch_logc_chat) value(?,?)")
    }

    override fun sopoOpen(ara: SocketAddress, protocol: Int, sopo: Array<Any>) {
        when(protocol) {
            // 로그인
            100 -> {

                send(ara, 200) // 로그인 승인
                send(ara, 201) // 로그인 실패
            }
            // 회원가입
            101 -> {

                send(ara, 202) // 회원가입 승인
                send(ara, 203) // 아이디 중복
            }
            // 채팅
            300 -> {
                val user = sopo[0] as String
                val chatlog = sopo[1] as String

                pr(user)
                pr(chatlog)

                cast.getValue("chat log").apply {
                    setString(1, user)
                    setString(2, chatlog)
                }.executeUpdate()
            }
        }
    }

    override fun sopoProtocol(ara: SocketAddress, protocol: Int) {
    }

    override fun usrKick(ara: SocketAddress) {
    }

    override fun wrErrLog(ara: SocketAddress, key: Int, code: Int) {
    }

    override fun wrLog(ara: SocketAddress, key: Int, protocol: Int) {
    }
}