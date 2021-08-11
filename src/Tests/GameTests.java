package Tests;

import Cards.Card;
import Cells.Cell;
import Cells.EstateCell;
import Gui.BoardCanvas;
import Gui.MurderM;
import Main.Game;
import Objects.Board;
import Objects.Player;
import Objects.Estate;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;


import java.util.*;

class GameTests {

    public MurderM generateGame(){
        Board board = new Board(24, 24);
        board.setup();
        BoardCanvas boardCanvas = new BoardCanvas(board, board.getCells(), board.getCellImages(), board.getWeapons(), board.getEstates(), board.getPlayers());
        boardCanvas.setSize(576, 576);
        return new MurderM(board, boardCanvas);
    }

    @Test
    @DisplayName("Players should be 0 before the game is started")
    void Test_1() {
        MurderM mm = generateGame();

        int playersSize = mm.getBoard().getPlayers().size();

        Assertions.assertEquals(0, playersSize);
    }

    @Test
    @DisplayName("Check the game has not be won")
    void Test_2() {
        MurderM mm = generateGame();
        Game g = new Game(mm);

        //g.playGame();

        Assertions.assertFalse(g.checkWin(new List<Card>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Card> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean add(Card card) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Card> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, Collection<? extends Card> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public Card get(int index) {
                return null;
            }

            @Override
            public Card set(int index, Card element) {
                return null;
            }

            @Override
            public void add(int index, Card element) {

            }

            @Override
            public Card remove(int index) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<Card> listIterator() {
                return null;
            }

            @Override
            public ListIterator<Card> listIterator(int index) {
                return null;
            }

            @Override
            public List<Card> subList(int fromIndex, int toIndex) {
                return null;
            }
        }));


    }

    @Test
    @DisplayName("Check the estate cells are being constructed correctly")
    void Test_3() {
        EstateCell ec = new EstateCell(0, 0, "Hello", "World", true, null);
        Assertions.assertEquals(ec.toString(), "HW");
        Assertions.assertTrue(ec.isDoor());

    }

    @Test
    @DisplayName("Check the estate cells dont return true to contain a cell that they dont")
    void Test_4() {
        Estate e = new Estate("MyEstate", new ArrayList<Integer>(Arrays.asList(6, 9)), 10, 10, null, 20, 20);
        Assertions.assertFalse(e.containsExit(new Cell(1, 1, null) {
            @Override
            public int getRow() {
                return super.getRow();
            }
        }));
    }


    @Test
    @DisplayName("Make sure teh clone function returns a an object")
    void Test_5() {
        Player p = new Player("Bob", 0, 0, null, "Henry", "Lost");
        Assertions.assertNotNull(p.clone());
    }

}