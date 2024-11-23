package com.example.ting.activity

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import okhttp3.*
import kotlin.coroutines.CoroutineContext

private fun splitYrcWords(str: String): List<String> {
    if (str.isEmpty()) {
        return emptyList()
    }
    val linkedList = mutableListOf<String>()
    val trim = str.trim()
    var i = 0
    var z = false
    var i2 = 0
    for (i3 in 0 until trim.length) {
        val charAt = trim[i3]
        if (charAt == '(') {
            z = true
            i2 = i3
        } else {
            if (z && charAt == ')') {
                if (i2 > i) {
                    linkedList.add(trim.substring(i, i2))
                    i = i2
                }
            } else if (charAt != ',') {
                if (charAt.code != 65292) {
                    if (charAt != ' ') {
                        if (charAt >= '0' && charAt <= '9') {
                        }
                    }
                }
            }
            z = false
        }
    }
    linkedList.add(trim.substring(i))
    return linkedList
}

val flow1 = flow {
    OkHttpClient.Builder()
        .build()
        .newCall(
            Request.Builder()
                .url("https://fakeresponder.com/?sleep=2000")
                .build()
        ).execute().body?.string()?.let {
            emit(it)
        }
}

suspend fun main() {
    GlobalScope.launch {
        println(coroutineContext)
    }
    delay(10000)
//    println("(420,454,0)Seventeen (874,0,0)- (874,50,0)或(924,0,0)许(924,51,0)吧".split("(").joinToString("--"))
//    val str = "(45229,346,0)똑같은 (45575,304,0)곳을 (45879,389,0)또 (46268,559,0)헤매고 (46827,635,0)있어"
//    "\\(([\\d,，]+)\\)(.*)".toRegex()
//        .findAll(str)
//        .forEach {
//            println(it.groupValues)
//        }
//    "\\(([\\d,，]+)\\)[^()]*".toRegex()
//        .findAll(str)
//        .forEach {
//            println(it.groupValues)
//        }
//"(45229,346,0)똑같은 (45575,304,0)곳을 (45879,389,0)또 (46268,559,0)헤매고 (46827,635,0)있어"
//    Lyric(
//        yrc = Lyric.Lrc(
//            "[45229,2233](45229,346,0)똑같은 (45575,304,0)곳을 (45879,389,0)또 (46268,559,0)헤매고 (46827,635,0)있어"
//        ),
//    ).parse()
//    Lyric(
//        lrc = Lyric.Lrc(
//            "[00:00.000] 作词 : WOOZI/S.COUPS/VERNON\n[00:00.440] 作曲 : WOOZI/Lish\n[00:00.880] 编曲 : Lish\n[00:01.320]编曲：리시\n[00:14.710]들어가자 잠깐\n[00:16.680]예민한 날이 선 시계\n[00:18.430]소리 나는 방\n[00:19.830]궁금증에 비해\n[00:21.030]다소 짧아진 물음은\n[00:23.300]정적을 깨 대화 속엔\n[00:25.170]불만스런 느낌들이 가득 차\n[00:27.380]넘쳐나 더 이상은 안돼\n[00:29.510]한숨에 묻혀버린\n[00:31.050]주변은 고요해\n[00:32.590]숨 막힐 듯해도\n[00:33.860]입 다물고 있는 게\n[00:35.130]차라리 속 편해\n[00:36.800]딱딱해진 관계\n[00:38.540]해소되지 않은 싸움\n[00:39.670]끝내 열리는 방문 원점이\n[00:41.890]돼 고개 돌린 채\n[00:43.590]다음에 얘기해\n[00:44.920]똑같은 곳을 또 헤매고 있어\n[00:47.570]모든 게 다 낯설게만 느껴져\n[00:51.470]이제는 정말 끝내야 될까\n[00:55.220]글쎄 잘 몰라 나도 잘 몰라\n[00:59.200]또 눈물이 흘러\n[01:02.150]뭣 모를 눈물이 흘러\n[01:06.250]예전의 너와 나 그리운 걸까 왜\n[01:10.510]글쎄 잘 몰라 나도 잘 몰라\n[01:14.080]또 눈물이 흘러\n[01:17.140]한없이 눈물이 흘러\n[01:21.680]이제는 정말 끝내야 될까 널\n[01:25.500]글쎄 잘 몰라 나도 잘 몰라\n[01:32.220]Daydreaming in the midst of the night\n[01:35.310]You brush my thoughts\n[01:36.150]And sweep my sleep away\n[01:38.780]이제 와서 I miss all the times\n[01:41.490]불필요해 there are plenty more times\n[01:44.200]We'll miss anyway\n[01:44.990]It's painful to face you\n[01:47.020]듣고 싶어 하는 답변\n[01:48.790]알면서도 괜히\n[01:50.400]삐뚤어지는 감정\n[01:52.170]다시 또 비꼬이는\n[01:54.450]흉터와 죄책감\n[01:57.650]조여오는 벽면의 폐쇄감\n[01:59.650]똑같은 곳을 또 헤매고 있어\n[02:02.350]모든 게 다 낯설게만 느껴져\n[02:06.430]이제는 정말 끝내야 될까\n[02:10.380]글쎄 잘 몰라 나도 잘 몰라\n[02:14.090]또 눈물이 흘러\n[02:17.270]뭣 모를 눈물이 흘러\n[02:21.410]예전의 너와\n[02:23.010]나 그리운 걸까 왜\n[02:25.440]글쎄 잘 몰라 나도 잘 몰라\n[02:28.900]또 눈물이 흘러\n[02:32.210]한없이 눈물이 흘러\n[02:36.160]이제는 정말 끝내야 될까 널\n[02:40.430]글쎄 잘 몰라 나도 잘 몰라\n[02:44.610]서로 어긋나있는 길 건너편\n[02:47.430]멍하니 서 있는 너에게 물어\n[02:51.500]다시 되돌아갈 수 없는 걸까\n[02:55.210]글쎄 잘 몰라 나도 잘 몰라\n[02:59.580]서로 어긋나있는 길 건너편\n[03:02.540]멍하게 서 있는 너에게 물어\n[03:06.380]다시 되돌아갈 수 없는 걸까\n[03:10.240]글쎄 잘 몰라 나도 잘 몰라\n[03:17.780]또 눈물이 흘러\n[03:21.280]멈추지 않고서 흘러\n[03:24.970]알 것 만 같아\n[03:26.950]아니 이젠 알아\n[03:28.840]너 아님 안 돼 보내면 안 돼\n[03:32.870]왜 이제서야 난\n[03:35.850]네 모습이 보이는지\n[03:40.490]멀어진 날 안아 줄 수 있을까\n[03:43.820]글쎄 잘 몰라 나도 잘 몰라\n"
//        ),
//        tlyric = Lyric.Lrc(
//            "[by:玛格丽特还是长岛冰茶]\n[00:01.320]\n[00:14.710]等一下再进去吧\n[00:16.680]敏锐的天线表\n[00:18.430]从我房间发出的声音\n[00:19.830]与好奇心相比\n[00:21.030]有些问题是越来越少\n[00:23.300]打破沉寂的对话\n[00:25.170]令人不满的感觉\n[00:27.380]我不能再这样下去了\n[00:29.510]把叹息声埋了\n[00:31.050]周围很安静\n[00:32.590]好像快窒息一样\n[00:33.860]闭上你的嘴巴\n[00:35.130]还不如安心下来\n[00:36.800]坚不可摧的关系\n[00:38.540]消除不了的战争\n[00:39.670]结束这次来访\n[00:41.890]一点又回到原点\n[00:43.590]下次再说\n[00:44.920]又在同样的地方徘徊\n[00:47.570]一切都是陌生的感觉\n[00:51.470]现在真的结束了吗\n[00:55.220]这个真的不知道 我也不知道\n[00:59.200]又流下眼泪\n[01:02.150]还不知道眼泪已流下\n[01:06.250]想念以前的你和我 为什么\n[01:10.510]这个真的不知道 我也不知道\n[01:14.080]又流下眼泪\n[01:17.140]已经没有眼泪可流\n[01:21.680]现在你真的结束了吗\n[01:25.500]这个真的不知道 我也不知道\n[01:32.220]在黑夜里做白日梦\n[01:35.310]你冲刷我的想法\n[01:36.150]和扫荡我的睡眠\n[01:38.780]现在 我错过了所有的时间\n[01:41.490]没有必要 还有很多次\n[01:44.200]反正我们也会想念\n[01:44.990]面对你是痛苦的\n[01:47.020]我想听到的回答\n[01:48.790]你知道的\n[01:50.400]扭曲的感情\n[01:52.170]再次讽刺的\n[01:54.450]那些疤痕的负罪感\n[01:57.650]束缚的墙面的封闭感\n[01:59.650]又在同样的地方徘徊\n[02:02.350]一切都是陌生的感觉\n[02:06.430]现在真的结束了吗\n[02:10.380]这个真的不知道 我也不知道\n[02:14.090]又流下眼泪\n[02:17.270]还不知道眼泪已流下\n[02:21.410]想念以前的你和我\n[02:23.010]为什么\n[02:25.440]这个真的不知道 我也不知道\n[02:28.900]又流下眼泪\n[02:32.210]已经没有眼泪可流\n[02:36.160]现在你真的结束了吗\n[02:40.430]这个真的不知道 我也不知道\n[02:44.610]相互站在结束的路的对面\n[02:47.430]呆呆地向你发问\n[02:51.500]我们真的回不去了吗\n[02:55.210]这个真的不知道 我也不知道\n[02:59.580]相互站在结束的路的对面\n[03:02.540]呆呆地向你发问\n[03:06.380]我们真的回不去了吗\n[03:10.240]这个真的不知道 我也不知道\n[03:17.780]又流下眼泪\n[03:21.280]不停地流\n[03:24.970]和以前一样\n[03:26.950]我现在知道了\n[03:28.840]我还是不行 不能送你走\n[03:32.870]为什么现在我\n[03:35.850]才看到你的模样\n[03:40.490]远去的那些我数不清\n[03:43.820]这个真的不知道 我也不知道"
//        )
//    ).parse()

//
//    coroutineScope {
//        KTV.init {
//            onDownloadProgress {
//                println("start$it")
//            }
//        }
//        delay(1000)
//        KTV.setValue(123)
//        delay(1000)
//        KTV.onDownloadProgress {
//            println("onDownloadProgress$it")
//        }
//        delay(1000)
//        KTV.setValue(456)
//    }
//    delay(10000)
}

class Test2(
) : CoroutineScope {
    var onProgress: ((Int) -> Unit)? = null
    var i = 0

    val playPosition = flow {
        while (true) {
            emit(i++)
            delay(100)
        }
    }.catch {
//        Logz.tag(TAG).e(it)
    }.flowOn(Dispatchers.IO).stateIn(this, SharingStarted.Lazily, 0)

    init {
        playPosition.launchIn(this)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}

val flow = flow {
    while (true && i != 3) {
        emit(i)
        i++
        delay(1000)
    }
}

private val isStartPolling: MutableSharedFlow<Pair<Boolean, Long>> = MutableSharedFlow(
    replay = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
var i = 1
val roomBattleUiState: StateFlow<Int?> = isStartPolling.flatMapLatest {
    flow {
        while (it.first) {
            emit(i)
            i++
            if (i == 3) {
                return@flow
            }
            delay(3000)
        }
        emit(null)
    }
}.filterNotNull().onEach {
    println("onEach$it")
    delay(1000)
    println("clear")
}.retry {
    true
}.flowOn(Dispatchers.IO).stateIn(GlobalScope, SharingStarted.WhileSubscribed(5000), 0)