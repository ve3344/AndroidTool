package me.lwb.androidtool.library.shell

enum class Platform(private val description: String, private  val key: String) {
    Linux("Linux", "linux"),
    Mac_OS("Mac OS", "mac"),
    Mac_OS_X("Mac OS X", "mac"),
    Windows("Windows", "windows"),
    OS2("OS/2", "os/2"),
    Solaris("Solaris", "solaris"),
    SunOS("SunOS", "sunos"),
    MPEiX("MPE/iX", "mpe/ix"),
    HP_UX("HP-UX", "hp-ux"),
    AIX("AIX", "aix"),
    OS390("OS/390", "os/390"),
    FreeBSD("FreeBSD", "freebsd"),
    Irix("Irix", "irix"),
    Digital_Unix("Digital Unix", "digital"),
    NetWare_411("NetWare", "netware"),
    OSF1("OSF1", "osf1"),
    OpenVMS("OpenVMS", "openvms"),
    Others("Others", "-others-");

    override fun toString(): String {
        return description
    }

    companion object {
        val currentPlatform: Platform by lazy {
            val osName = System.getProperty("os.name").lowercase()
            if ("mac" in osName&& "os" in osName){
                return@lazy if ("x" in osName) Mac_OS_X else Mac_OS
            }

            values().firstOrNull { it.key in osName } ?: Others
        }
    }
}