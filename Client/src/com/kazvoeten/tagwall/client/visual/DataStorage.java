/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kazvoeten.tagwall.client.visual;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Kaz Voeten
 */
public class DataStorage {

    private static DataStorage storage;
    private ArrayList<String> quotes = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock(true);

    public static DataStorage getStorage() {
        if (storage == null) {
            storage = new DataStorage();
        }
        return storage;
    }

    public String getRandomQuote() {
        return quotes.get(ThreadLocalRandom.current().nextInt(0, quotes.size()));
    }
    
    public void SetQuotes(ArrayList<String> newQuotes) {
        quotes.clear();
        for (String quote: newQuotes) {
            quotes.add(quote);
        }
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }
}
