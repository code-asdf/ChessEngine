import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
// a good chess engine has a good sorting technique and a good rating technique
public class alphaBetaChess {
	static String chessBoard[][] = {
			{"r", "k", "b", "q", "a", "b", "k", "r"},
			{"p", "p", "p", "p", "p", "p", "p", "p"},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{"P", "P", "P", "P", "P", "P", "P", "P"},
			{"R", "K", "B", "Q", "A", "B", "K", "R"}};
	static int kingPositionC, kingPositionL;
	static int humanAsWhite = -1; //1= humans as white,0=humans as black
	static int globalDepth = 4;
	public static void main(String[] args) {
		while (!"A".equals(chessBoard[kingPositionC/8][kingPositionC%8])) {kingPositionC++;}//get King's location
		while (!"a".equals(chessBoard[kingPositionL/8][kingPositionL%8])) {kingPositionL++;}//get king's location

		JFrame f = new JFrame("CHESS");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UserInterface ui =new UserInterface();
		f.add(ui);
		f.setSize(757,570);
		f.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-f.getWidth())/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-f.getHeight())/2);
		f.setVisible(true);
		ui.addMouseListener(ui);
		ui.addMouseMotionListener(ui);
		System.out.println(possibleMoves());
		Object[] option = {"Computer","Human"};
		humanAsWhite = JOptionPane.showOptionDialog(null,"Who should play as White?","ABC Options", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,null,option,option[1]);
		if(humanAsWhite==0){
			long startTime = System.currentTimeMillis();
			makeMove(alphaBeta(globalDepth,1000000,-1000000,"",0));
			long endTime = System.currentTimeMillis();
			System.out.println("That took " +(endTime-startTime)+" milliseconds  ");
			flipBoard();
			f.repaint();
		}

		for(int i=0;i<8;i++){
			System.out.println(Arrays.toString(chessBoard[i]));
		}
	}
	public static String alphaBeta(int depth,int beta,int alpha,String move,int player){
		//return in the form of 1234b########
		String list  = possibleMoves();
		if(depth==0 || list.length()==0){
			return move +(Rating.rating(list.length(),depth)*(2*player-1));
		}
		list=sortMove(list);
		player = 1-player; // either 1 or 0
		for(int i=0;i<list.length();i+=5){
			makeMove(list.substring(i,i+5));
			flipBoard();
			String returnString = alphaBeta(depth-1, beta, alpha,list.substring(i,i+5), player);
			int value = Integer.valueOf(returnString.substring(5));
			flipBoard();
			undoMove(list.substring(i,i+5));
			if(player==0){
				if(value<=beta){
					beta = value;
					if(depth==globalDepth){
						move = returnString.substring(0,5);
					}
				}
			}else{
				if(value>alpha){
					alpha = value;
					if(depth==globalDepth){
						move = returnString.substring(0,5);
					}
				}
			}
			if(alpha>=beta){
				if(player==0){
					return move+beta;
				}else{
					return move + alpha;
				}
			}
		}
		if(player==0){
			return move + beta;
		}else{
			return move + alpha;
		}

	}
	public static void flipBoard(){
		String temp;	//improvement we can only swap elements with peices
		for(int i=0;i<32;i++){
			int r = i/8, c= i%8;
			if(Character.isUpperCase(chessBoard[r][c].charAt(0))){
				temp = chessBoard[r][c].toLowerCase();
			}else{
				temp = chessBoard[r][c].toUpperCase();
			}
			if(Character.isUpperCase(chessBoard[7-r][7-c].charAt(0))){
				chessBoard[r][c] = chessBoard[7-r][7-c].toLowerCase();
			}else{
				chessBoard[r][c] = chessBoard[7-r][7-c].toUpperCase();
			}
			chessBoard[7-r][7-c] = temp;
		}
		int kingTemp = kingPositionC;
		kingPositionC = 63-kingPositionL;
		kingPositionL = 63-kingTemp;
	}
	public static void makeMove(String move){
		if(move.charAt(4)!='P'){
			//x1,y1,x2,y2,captured piece
			chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))] = chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))] ;
			chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))] = " ";
			if("A".equals(chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))])){
				kingPositionC = 8*Character.getNumericValue(move.charAt(2))+Character.getNumericValue(move.charAt(3));
			}
		}else{
			//column1,column2,captured-piece,new-piece,P
			//if pawn promotion
			chessBoard[1][Character.getNumericValue(move.charAt(0))] = " ";
			chessBoard[0][Character.getNumericValue(move.charAt(1))] = String.valueOf(move.charAt(3));
		}
	}
	public static void undoMove(String move){
		if(move.charAt(4)!='P'){

			chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))] = chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))] ;
			chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))] = String.valueOf(move.charAt(4));
			if("A".equals(chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))])){
				kingPositionC = 8*Character.getNumericValue(move.charAt(0))+Character.getNumericValue(move.charAt(1));
			}
		}else{

			//if pawn promotion
			chessBoard[1][Character.getNumericValue(move.charAt(0))] = "P";
			chessBoard[0][Character.getNumericValue(move.charAt(1))] = String.valueOf(move.charAt(2));
		}
	}

	public static String possibleMoves() {
		String list = "";
		for (int i = 0; i < 64; i++) {
			switch (chessBoard[i / 8][i % 8]) {
				case "P":
					list += posibleP(i);
					break;
				case "R":
					list += posibleR(i);
					break;
				case "K":
					list += posibleK(i);
					break;
				case "B":
					list += posibleB(i);
					break;
				case "Q":
					list += posibleQ(i);
					break;
				case "A":
					list += posibleA(i);
					break;
			}
		}
		return list;
	}


	public static String posibleP(int i) {
		String list="",oldPiece;
		int r = i/8,c=i%8;
		//movement
		try{
			if(" ".equals(chessBoard[r-1][c])){
				//normal forward movement
				if(r!=1){
					oldPiece = chessBoard[r-1][c];
					chessBoard[r-1][c] = "P";
					chessBoard[r][c] = " ";
					if(kingSafe()){
						list = list + r +c +(r-1)+c + oldPiece;
					}
					chessBoard[r-1][c] = " ";
					chessBoard[r][c] = "P";
					//base position then double movement possibility
					if(r==6){
						if(" ".equals(chessBoard[r-2][c])){
							oldPiece = chessBoard[r-2][c];
							chessBoard[r-2][c] = "P";
							chessBoard[r][c] = " ";
							if(kingSafe()){
								list = list + r +c +(r-2)+c + oldPiece;
							}
							chessBoard[r-2][c] = oldPiece;
							chessBoard[r][c] = "P";
						}
					}
				}
				else{
					String[] temp={"Q","R","B","K"};
					for (int k=0; k<4; k++) {
						oldPiece=chessBoard[r-1][c];
						chessBoard[r][c]=" ";
						chessBoard[r-1][c]=temp[k];
						if (kingSafe()) {
							//column1,column2,captured-piece,new-piece,P
							list=list+c+c+oldPiece+temp[k]+"P";		//to be changed
						}
						chessBoard[r][c]="P";
						chessBoard[r-1][c]=oldPiece;
					}

				}
			}
		}catch(Exception e){}

		for (int j=-1; j<=1; j+=2) {
			try {//capture
				if (Character.isLowerCase(chessBoard[r-1][c+j].charAt(0)) && i>=16) {
					oldPiece=chessBoard[r-1][c+j];
					chessBoard[r][c]=" ";
					chessBoard[r-1][c+j]="P";
					if (kingSafe()) {
						list=list+r+c+(r-1)+(c+j)+oldPiece;
					}
					chessBoard[r][c]="P";
					chessBoard[r-1][c+j]=oldPiece;
				}
			} catch (Exception e) {}
			try {//promotion && capture
				if (Character.isLowerCase(chessBoard[r-1][c+j].charAt(0)) && i<16) {
					String[] temp={"Q","R","B","K"};
					for (int k=0; k<4; k++) {
						oldPiece=chessBoard[r-1][c+j];
						chessBoard[r][c]=" ";
						chessBoard[r-1][c+j]=temp[k];
						if (kingSafe()) {
							//column1,column2,captured-piece,new-piece,P
							list=list+c+(c+j)+oldPiece+temp[k]+"P";
						}
						chessBoard[r][c]="P";
						chessBoard[r-1][c+j]=oldPiece;
					}
				}
			} catch (Exception e) {}
		}
		return list;
	}
	public static String posibleR(int i) {
		String list="",oldPiece;
		int r =i/8,c=i%8;
		int temp =1;
		for(int j=-1;j<=1;j+=2){
			try{
				while(" ".equals(chessBoard[r][c+temp*j])){
					oldPiece=chessBoard[r][c+temp*j];
					chessBoard[r][c] = " ";
					chessBoard[r][c+temp*j] = "R";
					if(kingSafe()){
						list = list + r + c + r + (c+temp*j) + oldPiece;
					}
					chessBoard[r][c] ="R";
					chessBoard[r][c+temp*j]=oldPiece;
					temp++;
				}
				if(Character.isLowerCase(chessBoard[r][c+temp*j].charAt(0))){
					oldPiece=chessBoard[r][c+temp*j];
					chessBoard[r][c] = " ";
					chessBoard[r][c+temp*j] = "R";
					if(kingSafe()){
						list = list + r + c + r + (c+temp*j) + oldPiece;
					}
					chessBoard[r][c] ="R";
					chessBoard[r][c+temp*j]=oldPiece;
				}
			}catch(Exception e){}
			temp=1;
			try{
				while(" ".equals(chessBoard[r+temp*j][c])){
					oldPiece=chessBoard[r+temp*j][c];
					chessBoard[r][c] = " ";
					chessBoard[r+temp*j][c] = "R";
					if(kingSafe()){
						list = list + r + c + (r+temp*j)+ (c) + oldPiece;
					}
					chessBoard[r][c] ="R";
					chessBoard[r+temp*j][c]=oldPiece;
					temp++;
				}
				if(Character.isLowerCase(chessBoard[r+temp*j][c].charAt(0))){
					oldPiece=chessBoard[r+temp*j][c];
					chessBoard[r][c] = " ";
					chessBoard[r+temp*j][c] = "R";
					if(kingSafe()){
						list = list + r + c + (r+temp*j) + c + oldPiece;
					}
					chessBoard[r][c] ="R";
					chessBoard[r+temp*j][c]=oldPiece;
				}
			}catch(Exception e){}
			temp=1;
		}
		return list;
	}
	public static String posibleK(int i) {
		String list="",oldPiece;
		int r =i/8,c=i%8;
		for(int j=-1;j<=1;j+=2){
			for(int k=-1;k<=1;k+=2){
				try{
					if(Character.isLowerCase(chessBoard[r+j][c+2*k].charAt(0)) || " ".equals(chessBoard[r+j][c+2*k])){
						oldPiece = chessBoard[r+j][c+2*k];
						chessBoard[r+j][c+2*k] = "K";
						chessBoard[r][c] = " ";
						if (kingSafe()) {
							list = list + r + c + (r+j) + (c+2*k) + oldPiece;
						}
						chessBoard[r+j][c+2*k] = oldPiece;
						chessBoard[r][c] = "K";
					}
				}catch(Exception e){}
				try{
					if(Character.isLowerCase(chessBoard[r+2*k][c+j].charAt(0)) || " ".equals(chessBoard[r+2*k][c+j])){
						oldPiece = chessBoard[r+2*k][c+j];
						chessBoard[r+2*k][c+j] = "K";
						chessBoard[r][c] = " ";
						if (kingSafe()) {
							list = list + r + c + (r+2*k) + (c+j) + oldPiece;
						}
						chessBoard[r+2*k][c+j] = oldPiece;
						chessBoard[r][c] = "K";
					}
				}catch(Exception e){}
			}
		}
		return list;
	}
	public static String posibleB(int i) {
		String list="",oldPiece;
		int r =i/8,c=i%8;
		int temp = 1;
		for(int j=-1;j<=1;j+=2){
			for(int k = -1;k<=1;k+=2){
				try{
					while(" ".equals(chessBoard[r+temp*j][c+temp*k])){
						oldPiece = chessBoard[r+temp*j][c+temp*k];
						chessBoard[r][c]=" ";
						chessBoard[r+temp*j][c+temp*k]="B";
						if (kingSafe()) {
							list = list + r + c + (r+temp*j) + (c+temp*k) + oldPiece;
						}
						chessBoard[r][c] = "B";
						chessBoard[r+temp*j][c+temp*k]=oldPiece;
						temp++;
					}
					if(Character.isLowerCase(chessBoard[r+temp*j][c+temp*k].charAt(0))){
						oldPiece = chessBoard[r+temp*j][c+temp*k];
						chessBoard[r][c]=" ";
						chessBoard[r+temp*j][c+temp*k]="B";
						if (kingSafe()) {
							list = list + r + c + (r+temp*j) + (c+temp*k) + oldPiece;
						}
						chessBoard[r][c] = "B";
						chessBoard[r+temp*j][c+temp*k]=oldPiece;
					}
				}catch(Exception e){}
				temp=1;
			}
		}
		return list;
	}
	public static String posibleQ(int i) {
		String list="",oldPiece;
		int r =i/8,c=i%8;
		int temp = 1;
		for(int j=-1;j<=1;j++){
			for(int k = -1;k<=1;k++){
				if(j!=0 || k!=0){
					try{
						while(" ".equals(chessBoard[r+temp*j][c+temp*k])){
							oldPiece = chessBoard[r+temp*j][c+temp*k];
							chessBoard[r][c]=" ";
							chessBoard[r+temp*j][c+temp*k]="Q";
							if (kingSafe()) {
								list = list + r + c + (r+temp*j) + (c+temp*k) + oldPiece;
							}
							chessBoard[r][c] = "Q";
							chessBoard[r+temp*j][c+temp*k]=oldPiece;
							temp++;
						}
						if(Character.isLowerCase(chessBoard[r+temp*j][c+temp*k].charAt(0))){
							oldPiece = chessBoard[r+temp*j][c+temp*k];
							chessBoard[r][c]=" ";
							chessBoard[r+temp*j][c+temp*k]="Q";
							if (kingSafe()) {
								list = list + r + c + (r+temp*j) + (c+temp*k) + oldPiece;
							}
							chessBoard[r][c] = "Q";
							chessBoard[r+temp*j][c+temp*k]=oldPiece;
							temp++;
						}
					}catch(Exception e){}
					temp=1;
				}
			}
		}
		return list;
	}
	public static String posibleA(int i) {
		String list="",oldPeice;
		int r = i/8,c = i%8;
		for(int j=0;j<9;j++){
			if(j!=4){
				try {
					if (Character.isLowerCase(chessBoard[r - 1 + j / 3][c - 1 + j % 3].charAt(0))
							|| chessBoard[r - 1 + j / 3][c - 1 + j % 3].equals(" ")) {
						oldPeice = chessBoard[r - 1 + j / 3][c - 1 + j % 3];
						chessBoard[r][c] = " ";
						chessBoard[r - 1 + j / 3][c - 1 + j % 3] = "A";
						int kingTemp = kingPositionC;
						kingPositionC = i + (j / 3)*8 + j % 3 - 9;
						if (kingSafe()) {
							list = list + r + c + (r - 1 + j / 3) + (c - 1 + j % 3) + oldPeice;
						}
						chessBoard[r][c] = "A";
						chessBoard[r - 1 + j / 3][c - 1 + j % 3] = oldPeice;
						kingPositionC = kingTemp;
					}
				} catch (Exception e) {

				}
			}
		}
		// need to add castling later
		return list;
	}

	public static String sortMove(String list){
		int[] score = new int[list.length()/5];
		for(int i=0;i<list.length();i+=5){
			makeMove(list.substring(i,i+5));
			score[i/5] = -Rating.rating(-1,0);
			undoMove(list.substring(i,i+5));
		}
		String newListA="";
		String newListB=list;
		for(int i=0;i<Math.min(6,list.length()/5);i++){//first few move only
			int max=-1000000,maxLocatuon=0;
			for(int j=0;j<list.length()/5;j++){
				if(score[j]>max){
					max = score[j];
					maxLocatuon=j;
				}
			}
			score[maxLocatuon] = -1000000;
			newListA += list.substring(maxLocatuon*5,maxLocatuon*5+5);
			newListB = newListB.replace(list.substring(maxLocatuon*5,maxLocatuon*5+5),"");
		}
		return newListA+newListB;
	}
	public static boolean kingSafe() {
		//bishop/queen
		int temp =1;
		for(int i =-1;i<=1;i+=2){
			for(int j=-1;j<=1;j+=2){
				try{
					while(" ".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8 + temp*j])){temp++;}
					if("b".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8 + temp*j]) ||
							"q".equals(chessBoard[kingPositionC/8+temp*i][kingPositionC%8 + temp*j])){
						return false;
					}
				}catch(Exception e){}
				temp =1;
			}
		}
		//rook/queen
		for(int i=-1;i<=1;i+=2){
			try{
				while(" ".equals(chessBoard[kingPositionC/8][kingPositionC%8 + temp*i])){temp++;}
				if("r".equals(chessBoard[kingPositionC/8][kingPositionC%8 + temp*i]) ||
						"q".equals(chessBoard[kingPositionC/8][kingPositionC%8 + temp*i]) ){
					return false;
				}
			}catch(Exception e){}
			temp = 1;
			try{
				while(" ".equals(chessBoard[kingPositionC/8+ temp*i][kingPositionC%8])){temp++;}
				if("r".equals(chessBoard[kingPositionC/8+ temp*i][kingPositionC%8]) ||
						"q".equals(chessBoard[kingPositionC/8+ temp*i][kingPositionC%8]) ){
					return false;
				}
			}catch(Exception e){}
			temp=1;
		}
		//knight
		for(int i=-1;i<=1;i+=2){
			for(int j=-1;j<=1;j+=2){
				try {
					if ("k".equals(chessBoard[kingPositionC/8+i][kingPositionC%8+j*2])) {
						return false;
					}
				} catch (Exception e) {}
				try {
					if ("k".equals(chessBoard[kingPositionC/8+j*2][kingPositionC%8+i])) {
						return false;
					}
				} catch (Exception e) {}
			}
		}
		//pawn
		if(kingPositionC>=16){
			try{
				if("p".equals(chessBoard[kingPositionC/8-1][kingPositionC%8-1])){
					return false;
				}
			}catch(Exception e){}
			try{
				if("p".equals(chessBoard[kingPositionC/8-1][kingPositionC%8+1])){
					return false;
				}
			}catch(Exception e){}
		}
		//king
		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				if(i!=0 || j!=0){
					try{
						if("a".equals(chessBoard[kingPositionC/8+i][kingPositionC%8+j])){
							return false;
						}
					}catch(Exception e){}
				}
			}
		}
		return true;
	}
}