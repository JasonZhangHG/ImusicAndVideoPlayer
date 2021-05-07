package com.music.player.lib.manager;

import java.util.Observable;

/**
 * 409
 * 2019/3/5.
 * Observable
 */

public class MusicSubjectObservable extends Observable {

    public MusicSubjectObservable(){

    }
    public void updataSubjectObserivce(Object data){
        setChanged();
        notifyObservers(data);
    }
}
