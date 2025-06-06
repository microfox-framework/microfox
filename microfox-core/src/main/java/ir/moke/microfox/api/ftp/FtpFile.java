package ir.moke.microfox.api.ftp;

import java.util.Calendar;

public record FtpFile(int type,
                      int hardLinkCount,
                      long size,
                      String rawListing,
                      String user,
                      String group,
                      String name,
                      String link,
                      Calendar calendar) {
}
