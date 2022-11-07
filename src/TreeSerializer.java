public interface TreeSerializer {

	public String serialize(Node root);
	public Node deserialize(String str);

}
