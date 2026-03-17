package cc.pineclone.automation;

import java.util.Locale;

public class NativeSystem {

    public static Family getFamily() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        Family family = Family.UNSUPPORTED;
        if (osName.equals("freebsd")) {
            family = Family.FREEBSD;
        } else if (osName.equals("openbsd")) {
            family = Family.OPENBSD;
        } else if (osName.equals("mac os x")) {
            family = Family.DARWIN;
        } else if (!osName.equals("solaris") && !osName.equals("sunos")) {
            if (osName.equals("linux")) {
                family = Family.LINUX;
            } else if (osName.startsWith("windows")) {
                family = Family.WINDOWS;
            }
        } else {
            family = Family.SOLARIS;
        }

        return family;
    }

    public static Arch getArchitecture() {
        String osArch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        Arch arch = Arch.UNSUPPORTED;
        if (osArch.startsWith("arm")) {
            arch = Arch.ARM;
        } else if (osArch.equals("aarch64")) {
            arch = Arch.ARM64;
        } else if (osArch.equals("sparc")) {
            arch = Arch.SPARC;
        } else if (osArch.equals("sparc64")) {
            arch = Arch.SPARC64;
        } else if (!osArch.equals("ppc") && !osArch.equals("powerpc")) {
            if (!osArch.equals("ppc64") && !osArch.equals("powerpc64")) {
                if (!osArch.equals("x86") && !osArch.equals("i386") && !osArch.equals("i486") && !osArch.equals("i586") && !osArch.equals("i686")) {
                    if (osArch.equals("x86_64") || osArch.equals("amd64") || osArch.equals("k8")) {
                        arch = Arch.x86_64;
                    }
                } else {
                    arch = Arch.x86;
                }
            } else {
                arch = Arch.PPC64;
            }
        } else {
            arch = Arch.PPC;
        }

        return arch;
    }

    public enum Family {
        FREEBSD,
        OPENBSD,
        DARWIN,
        SOLARIS,
        LINUX,
        WINDOWS,
        UNSUPPORTED;

        public String toString() {
            return super.toString().toLowerCase(Locale.ROOT);
        }
    }

    public enum Arch {
        ARM,
        ARM64,
        SPARC,
        SPARC64,
        PPC,
        PPC64,
        x86,
        x86_64,
        UNSUPPORTED;

        public String toString() {
            return super.toString().toLowerCase(Locale.ROOT);
        }
    }
    
}
