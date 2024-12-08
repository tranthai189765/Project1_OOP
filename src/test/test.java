package test;

public class test {
	private static String extractAuthor_id(String linkURL) {
        if (linkURL != null && linkURL.contains("https://x.com/")) {
            return "user_" + linkURL.substring(linkURL.indexOf("https://x.com/") + "https://x.com/".length(), linkURL.lastIndexOf("/status"));
        }
        return null; // Trả về null nếu URL không hợp lệ
    }
	public static void main(String[] args) {
		System.out.println(test.extractAuthor_id("https://x.com/InterlayHQ/status/1748374714974974172"));
	}

}
