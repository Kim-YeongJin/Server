import java.net.SocketAddress

fun main() {
    ChatServer()
}

class ChatServer : MacheteServer("jdbc:mariadb://127.0.0.1:3306/chat", "root", "0523","192.168.1.105",3000,  200 , 10240 , 10240) {

    override fun qaddRoom() {
        qadd("chat log", "insert ch_log_chat (ch_logc_user, ch_logc_chat) value(?,?)")
        qadd("log in", "insert ch_log_login (ch_logn_user) value(?)")
        qadd("make chatroom", "insert ch_room (ch_mail1, ch_mail2, ch_mail3) value(?, ?, ?)")
    }

    override fun sopoOpen(ara: SocketAddress, protocol: Int, sopo: Array<Any>) {

        when(protocol) {
            // 로그인
            100 -> {
                val userID = sopo[0] as String
                pr(userID)
                pr(ara)

                cast.getValue("log in").apply {
                    setString(1, userID)
                }.executeUpdate()

                online[userID] = ara
            }
            // 회원가입
            101 -> {
                send(ara, 202) // 회원가입 승인
                send(ara, 203) // 아이디 중복
            }
            // 채팅 기록
            300 -> {
                val user = sopo[0] as String
                val yourID = sopo[1] as String
                val chatlog = sopo[2] as String

                cast.getValue("chat log").apply {
                    setString(1, user)
                    setString(2, chatlog)
                }.executeUpdate()

                val yourIP = online[yourID]

                if(yourIP != null) {
                    pr(yourIP)
                    send(yourIP, 301, user,  chatlog)
                }
            }
            // 채팅방 생성 요청
            400 -> {
                val userID = sopo[0] as String
                val yourID = sopo[1] as String

                val yourIP = online[yourID]

                cast.getValue("make chatroom").apply {
                    setString(1, userID)
                    setString(2, yourID)
                    setString(3, "")
                }.executeUpdate()

                if(yourIP != null) {
                    send(yourIP, 401, userID, ara)
                }
            }

            501 -> {
                val yourID = sopo[0] as String

                val yourIP = online[yourID]

                if(yourIP != null) {
                    send(yourIP, 500)
                }
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