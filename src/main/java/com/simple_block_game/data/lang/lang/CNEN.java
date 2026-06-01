package com.simple_block_game.data.lang.lang;

public record CNEN(String cn, String en) {

    public static CNEN create(String cn, String en) {
        return new CNEN(cn, en);
    }
}
