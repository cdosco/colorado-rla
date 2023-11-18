
package us.freeandfair.corla.query;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.postgresql.copy.CopyManager;
import org.postgresql.copy.CopyOut;
import org.postgresql.core.BaseConnection;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.query.Query;

import us.freeandfair.corla.persistence.Persistence;

/** export queries **/
public class ExportQueries {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(ExportQueries.class);

  /** to use the hibernate jdbc connection **/
  public static class CSVWork implements Work {

    /** pg query string **/
    private final String query;

    /** where to send the csv data **/
    private final OutputStream os;

    /** instantiation **/
    public CSVWork(final String query, final OutputStream os) {

      this.query = query;
      this.os = os;
    }

    /** do the work **/
    @SuppressWarnings("PMD.PreserveStackTrace")
    public void execute(final Connection conn) throws java.sql.SQLException {
      try {
        final CopyManager cm = new CopyManager(conn.unwrap(BaseConnection.class));
        final String q = String.format("COPY (%s) TO STDOUT WITH CSV HEADER", this.query);
        // cm.copyOut(q, this.os);
        custCopyOut(q, this.os, cm);
      } catch (java.io.IOException e) {
        throw new java.sql.SQLException(e.getMessage());
      }
    }
  }

  /** no instantiation **/
  private ExportQueries() {
  };

  /**
   * write the resulting rows from the query, as json objects, to the
   * OutputStream
   **/
  public static void customOut(final String query, final OutputStream os) {
    final Session s = Persistence.currentSession();
    final String withoutSemi = query.replace(";", "");

    final String jsonQuery =
        String.format("SELECT cast(row_to_json(r) as text)" + " FROM (%s) r", withoutSemi);
    final Query q = s.createNativeQuery(jsonQuery).setReadOnly(true).setFetchSize(1000);

    // interleave an object separator (the comma and line break) into the stream
    // of json objects to create valid json thx!
    // https://stackoverflow.com/a/25624818
    final Stream<Object[]> results =
        q.stream().flatMap(i -> Stream.of(new String[] {",\n"}, i)).skip(1); // remove
                                                                             // the
                                                                             // first
                                                                             // separator

    // write json by hand to preserve streaming writes in case of big data
    try {
      os.write("[".getBytes(StandardCharsets.UTF_8));
      results.forEach(line -> {
        try {
          // the object array is the columns, but in this case there is only
          // one, so we take it at index 0
          os.write(line[0].toString().getBytes(StandardCharsets.UTF_8));
        } catch (java.io.IOException e) {
          LOGGER.error(e.getMessage());
        }
      });

      os.write("]".getBytes(StandardCharsets.UTF_8));
    } catch (java.io.IOException e) {
      // log it
      LOGGER.error(e.getMessage());
    }
  }

  /**
   * write the resulting rows from the query, as json objects, to the
   * OutputStream
   **/
  public static void jsonOut(final String query, final OutputStream os) {
    final Session s = Persistence.currentSession();
    final String withoutSemi = query.replace(";", "");
    final String jsonQuery =
        String.format("SELECT cast(row_to_json(r) as text)" + " FROM (%s) r", withoutSemi);
    final Query q = s.createNativeQuery(jsonQuery).setReadOnly(true).setFetchSize(1000);

    // interleave an object separator (the comma and line break) into the stream
    // of json objects to create valid json thx!
    // https://stackoverflow.com/a/25624818
    final Stream<Object[]> results =
        q.stream().flatMap(i -> Stream.of(new String[] {",\n"}, i)).skip(1); // remove
                                                                             // the
                                                                             // first
                                                                             // separator

    // write json by hand to preserve streaming writes in case of big data
    try {
      os.write("[".getBytes(StandardCharsets.UTF_8));
      results.forEach(line -> {
        try {
          // the object array is the columns, but in this case there is only
          // one, so we take it at index 0
          os.write(line[0].toString().getBytes(StandardCharsets.UTF_8));
        } catch (java.io.IOException e) {
          LOGGER.error(e.getMessage());
        }
      });

      os.write("]".getBytes(StandardCharsets.UTF_8));
    } catch (java.io.IOException e) {
      // log it
      LOGGER.error(e.getMessage());
    }
  }

  /** send query results to output stream as csv **/
  public static void csvOut(final String query, final OutputStream os) {
    final Session s = Persistence.currentSession();
    final String withoutSemi = query.replace(";", "");
    s.doWork(new CSVWork(withoutSemi, os));
  }

  /**
   * The directory listing of the sql resource directory on the classpath,
   * hopefully! I couldn't figure out how to do this from within a deployed jar,
   * so here we are
   **/
  public static List<String> getSqlFolderFiles() {
    final List<String> paths = new ArrayList<String>();
    final String folder = "sql";
    final String[] fileNames = {"batch_count_comparison.sql", "contest.sql",
        "contest_comparison.sql", "contest_selection.sql", "contests_by_county.sql",
        "tabulate.sql", "tabulate_county.sql", "upload_status.sql", "seed.sql"};
    for (final String f : fileNames) {
      paths.add(String.format("%s/%s", folder, f));
    }
    return paths;
  }

  /** remove path and ext leaving the file name **/
  private static String fileName(final String path) {
    final int slash = path.lastIndexOf('/') + 1;
    final int dot = path.lastIndexOf('.');
    return path.substring(slash, dot);
  }

  /** file contents to string **/
  // I respectfully disagree
  @SuppressWarnings({"PMD.AssignmentInOperand"})
  public static String fileContents(final String path) throws java.io.IOException {

    final StringBuilder contents = new StringBuilder();
    final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    try (final InputStream is = loader.getResourceAsStream(path);
        final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        final BufferedReader br = new BufferedReader(isr);) {
      String line;
      while ((line = br.readLine()) != null) {
        contents.append(line);
        contents.append('\n');
      }
    }
    return contents.toString();
  }

  /**
   * read files from resources/sql/ and return map with keys as file names
   * without extension and value as the file contents
   **/
  public static Map<String, String> sqlFiles() throws java.io.IOException {
    final Map files = new HashMap<String, String>();
    List<String> paths = getSqlFolderFiles();
    for (final String path : paths) {
      if (path.endsWith(".sql")) {
        files.put(fileName(path), fileContents(path));
      }
    }
    return files;
  }

  public static long custCopyOut(final String sql, OutputStream to, CopyManager cm)
      throws SQLException, IOException {
    byte[] buf;
    CopyOut cp = cm.copyOut(sql);
    try {
      while ((buf = cp.readFromCopy()) != null) {
        String s = new String(buf,Charset.forName("ASCII"));
        if (s.contains("\\\"")) {
          List<Byte> newBuf = new ArrayList<>();
          for (int i = 0; i < buf.length; i++) {
            byte b1 = buf[i];
            Character ch = (char) b1;
            if (!ch.equals('\\')) {
              newBuf.add(b1);
            }
          }
          byte[] result = new byte[newBuf.size()];
          for (int i = 0; i < newBuf.size(); i++) {
            result[i] = newBuf.get(i).byteValue();
          }
          to.write(result);
        } else {
          List<Byte> newBuf = new ArrayList<>();
          for (int i = 0; i < buf.length; i++) {
            byte b1 = buf[i];
            newBuf.add(b1);
          }
          byte[] result = new byte[newBuf.size()];
          for (int i = 0; i < newBuf.size(); i++) {
            result[i] = newBuf.get(i).byteValue();
          }
          to.write(result);
        }
      }
      return cp.getHandledRowCount();
    } catch (IOException ioEX) {
      // if not handled this way the close call will hang, at least in 8.2
      if (cp.isActive()) {
        cp.cancelCopy();
      }
      try { // read until exhausted or operation cancelled SQLException
        while ((buf = cp.readFromCopy()) != null) {
        }
      } catch (SQLException sqlEx) {
      } // typically after several kB
      throw ioEX;
    } finally { // see to it that we do not leave the connection locked
      if (cp.isActive()) {
        cp.cancelCopy();
      }
    }
  }

}
