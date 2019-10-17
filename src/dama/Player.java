/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shashki1;

import java.io.Serializable;

/**
 *
 * @author Spike
 */
public class Player implements Serializable{
    String jmeno;
    PieceType typ;

    public Player(String jmeno, PieceType typ) {
        this.jmeno = jmeno;
        this.typ = typ;
    }
    
}
