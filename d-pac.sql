-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Creato il: Ago 13, 2017 alle 09:01
-- Versione del server: 10.1.16-MariaDB
-- Versione PHP: 7.0.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `d-pac`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `gamematch`
--

CREATE TABLE `gamematch` (
  `MatchId` int(5) NOT NULL,
  `result` tinyint(1) NOT NULL,
  `date` date NOT NULL,
  `score` int(10) NOT NULL,
  `userId` varchar(100) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dump dei dati per la tabella `gamematch`
--

INSERT INTO `gamematch` (`MatchId`, `result`, `date`, `score`, `userId`) VALUES
(5, 0, '2017-08-01', 5432, 'giulia'),
(6, 1, '2017-08-07', 3442, 'giulia'),
(7, 0, '2017-08-01', 5432, 'giulia'),
(8, 1, '2017-08-07', 3442, 'giulia'),
(18, 1, '2017-08-12', 1234, 'giulia'),
(19, 1, '2017-08-12', 1234, 'sariiin');

-- --------------------------------------------------------

--
-- Struttura della tabella `user`
--

CREATE TABLE `user` (
  `username` varchar(100) COLLATE utf8_bin NOT NULL,
  `name` varchar(10) COLLATE utf8_bin NOT NULL,
  `email` varchar(100) COLLATE utf8_bin NOT NULL,
  `password` varchar(50) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dump dei dati per la tabella `user`
--

INSERT INTO `user` (`username`, `name`, `email`, `password`) VALUES
('giulia', '', 'lucchiguli@#', ''),
('sariiin', 'sara', 'bcuhkd@hefew.it', 'ciaociao');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `gamematch`
--
ALTER TABLE `gamematch`
  ADD PRIMARY KEY (`MatchId`),
  ADD KEY `userId` (`userId`) USING BTREE;

--
-- Indici per le tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `gamematch`
--
ALTER TABLE `gamematch`
  MODIFY `MatchId` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;
--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `gamematch`
--
ALTER TABLE `gamematch`
  ADD CONSTRAINT `gamematch_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`username`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
