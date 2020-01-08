import java.net.SocketAddress

fun main() {
    ChatServer()
}

class ChatServer : MacheteServer("jdbc:mariadb://127.0.0.1:3306/chat", "root", "0523","192.168.1.105",3000,  200 , 10240 , 10240) {

    override fun qaddRoom() {
        qadd("chat log", "insert ch_log_chat (ch_logc_user, ch_logc_chat) value(?,?)")
        qadd("log in", "insert ch_log_login (ch_logn_user) value(?)")
        qadd("make chatroom", "insert ch_room (ch_mail1, ch_mail2) value(?, ?)")
        qadd("chatroom list update", "insert ch_room_list (userId, ch_room_num) value(?, ?)")
    }

    override fun sopoOpen(ara: SocketAddress, protocol: Int, sopo: Array<Any>) {

        when(protocol) {
            // 로그인
            100 -> {
                try {
                    val temp = sopo[0] as ByteArray
                    val userID = sv_rsa.dec(temp)

                    cast.getValue("log in").apply {
                        setString(1, userID)
                    }.executeUpdate()

                    online[userID] = ara

                    online.forEach{(key,value) -> pr("key: {$key}. value : {$value}")}
                    send(ara, 101)
                }catch (e:Exception){
                    pr("에러 ${e.message}")
                    pr("에러 ${e.cause}")
                }


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
                }.executeUpdate()

                cast.getValue("chatroom list update").apply {
                    setString(1, userID)
                }

                if(yourIP != null) {
                    send(yourIP, 401, userID)
                }
            }
            // 채팅 요청 유저에게 상대방 수락 전송
            501 -> {
                val yourID = sopo[0] as String

                val yourIP = online[yourID]

                if(yourIP != null) {
                    send(yourIP, 500)
                }
            }
            // 단톡방 입장
            600 -> {
                val userID = sopo[0] as String

                multichat.forEach { (key, value) -> send(value, 601, userID) }

                multichat.put(userID, ara)

            }
            // 단톡방 퇴장
            602 -> {
                val userID = sopo[0] as String
                val userIP = online[userID]

                multichat.remove(userID, userIP)
            }
            // 단톡방 채팅
            603 -> {
                val userID = sopo[0] as String
                val chatlog = sopo[1] as String

                multichat.forEach { (key, value) -> send(value, 604, userID, chatlog)}
            }
        }
    }

    override fun sopoProtocol(ara: SocketAddress, protocol: Int) {
        when(protocol) {
            // 클라이언트에게 공개키 전송
            10 -> {
                try {
                    val rs = MctSecRSA(1024, Charsets.ISO_8859_1)
                    pr("알고리즘 : ${rs.publKey.algorithm}, 포맷 : ${rs.publKey.format}")
                    pr("퍼블릭 키 ${rs.publKey}")
                    send(ara, 11, rs.publKey.toString())
                }catch (e:Exception){
                    pr("에러 ${e.message}")
                    pr("에러 ${e.cause}")
                    pr("에러 ${e.localizedMessage}")
                    pr("에러 ${e.suppressed}")
                    e.stackTrace.forEach {
                        pr("에러 스택  $it")
                    }
                }


            }
        }
    }

    override fun usrKick(ara: SocketAddress) {
    }

    override fun wrErrLog(ara: SocketAddress, key: Int, code: Int) {
    }

    override fun wrLog(ara: SocketAddress, key: Int, protocol: Int) {
    }
}