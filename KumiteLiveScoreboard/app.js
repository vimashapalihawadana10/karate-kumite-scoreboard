// =====================================================
// FIREBASE IMPORTS
// =====================================================

import {
    initializeApp
}
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js";


import {
    getFirestore,
    doc,
    onSnapshot
}
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";



// =====================================================
// FIREBASE CONFIG
//
// IMPORTANT:
// Replace these values with the values Firebase gives
// you when you register the Web App.
// =====================================================

const firebaseConfig = {

    apiKey: "AIzaSyBAi5eoQhhYJmsc3R_6SF6_-Y9DV4drDZ4",

    authDomain:
        "timerandfight.firebaseapp.com",

    projectId:
        "timerandfight",

    storageBucket:
        "timerandfight.firebasestorage.app",

    messagingSenderId:
        "930254513831",

    appId:
        "1:930254513831:web:70d8cc36a613f458d81d82"
};



// =====================================================
// INITIALIZE FIREBASE
// =====================================================

const app =
    initializeApp(firebaseConfig);


const db =
    getFirestore(app);



// =====================================================
// GET HTML ELEMENTS
// =====================================================

// Names

const akaName =
    document.getElementById("akaName");

const aoName =
    document.getElementById("aoName");


// Scores

const akaScore =
    document.getElementById("akaScore");

const aoScore =
    document.getElementById("aoScore");


// Timer

const timer =
    document.getElementById("timer");


// Winning gap

const winningGap =
    document.getElementById("winningGap");


// Status

const fightStatus =
    document.getElementById("fightStatus");


// Connection

const connectionStatus =
    document.getElementById(
        "connectionStatus"
    );


// Senshu

const akaSenshu =
    document.getElementById("akaSenshu");

const aoSenshu =
    document.getElementById("aoSenshu");


// Winner overlay

const winnerOverlay =
    document.getElementById(
        "winnerOverlay"
    );

const winnerText =
    document.getElementById(
        "winnerText"
    );

const winnerName =
    document.getElementById(
        "winnerName"
    );

const winnerReason =
    document.getElementById(
        "winnerReason"
    );

const finalScore =
    document.getElementById(
        "finalScore"
    );



// =====================================================
// FIRESTORE DOCUMENT
//
// Android writes:
//
// liveFights/current
//
// Website listens to exactly the same document.
// =====================================================

const liveFightRef =
    doc(
        db,
        "liveFights",
        "current"
    );



// =====================================================
// REAL-TIME FIREBASE LISTENER
// =====================================================

onSnapshot(

    liveFightRef,

    (snapshot) => {


        // -----------------------------------------
        // Firebase connected
        // -----------------------------------------

        connectionStatus.innerText =
            "● LIVE";

        connectionStatus.className =
            "connection-status connected";



        // -----------------------------------------
        // Document does not exist
        // -----------------------------------------

        if (!snapshot.exists()) {

            connectionStatus.innerText =
                "No live fight found";

            return;
        }



        // Get Firebase data

        const data =
            snapshot.data();



        console.log(
            "Live fight updated:",
            data
        );



        // =========================================
        // PLAYER NAMES
        // =========================================

        akaName.innerText =
            data.akaName || "AKA";


        aoName.innerText =
            data.aoName || "AO";



        // =========================================
        // SCORES
        // =========================================

        akaScore.innerText =
            data.akaScore ?? 0;


        aoScore.innerText =
            data.aoScore ?? 0;



        // =========================================
        // TIMER
        // =========================================

        const seconds =
            Number(
                data.timerSeconds ?? 0
            );


        timer.innerText =
            formatTime(seconds);



        // =========================================
        // WINNING GAP
        // =========================================

        winningGap.innerText =
            data.winningGap ?? 6;



        // =========================================
        // SENSHU
        // =========================================

        updateSenshu(
            akaSenshu,
            data.akaSenshu
        );


        updateSenshu(
            aoSenshu,
            data.aoSenshu
        );



        // =========================================
        // AKA WARNINGS
        // =========================================

        updateWarning(
            "akaC1",
            data.akaC1
        );


        updateWarning(
            "akaC2",
            data.akaC2
        );


        updateWarning(
            "akaC3",
            data.akaC3
        );


        updateWarning(
            "akaHC",
            data.akaHC
        );


        updateWarning(
            "akaH",
            data.akaH
        );


        updateWarning(
            "akaK",
            data.akaK
        );



        // =========================================
        // AO WARNINGS
        // =========================================

        updateWarning(
            "aoC1",
            data.aoC1
        );


        updateWarning(
            "aoC2",
            data.aoC2
        );


        updateWarning(
            "aoC3",
            data.aoC3
        );


        updateWarning(
            "aoHC",
            data.aoHC
        );


        updateWarning(
            "aoH",
            data.aoH
        );


        updateWarning(
            "aoK",
            data.aoK
        );



        // =========================================
        // FIGHT STATUS
        // =========================================

        const status =
            data.status || "READY";


        updateFightStatus(
            status
        );



        // =========================================
        // WINNER SCREEN
        // =========================================

        if (status === "FINISHED") {

            showWinnerScreen(
                data
            );

        } else {

            hideWinnerScreen();
        }

    },


    // =================================================
    // FIREBASE ERROR
    // =================================================

    (error) => {

        console.error(
            "Firebase listener error:",
            error
        );


        connectionStatus.innerText =
            "Firebase connection error";


        connectionStatus.className =
            "connection-status error";
    }

);



// =====================================================
// FORMAT TIMER
//
// Example:
// 120 -> 02:00
// 59  -> 00:59
// =====================================================

function formatTime(
    totalSeconds
) {

    totalSeconds =
        Math.max(
            0,
            Math.floor(totalSeconds)
        );


    const minutes =
        Math.floor(
            totalSeconds / 60
        );


    const seconds =
        totalSeconds % 60;


    return (
        String(minutes)
            .padStart(2, "0")
        +
        ":"
        +
        String(seconds)
            .padStart(2, "0")
    );
}



// =====================================================
// UPDATE SENSHU
// =====================================================

function updateSenshu(
    element,
    active
) {

    if (active === true) {

        element.classList.add(
            "active"
        );

    } else {

        element.classList.remove(
            "active"
        );
    }
}



// =====================================================
// UPDATE WARNING
// =====================================================

function updateWarning(
    elementId,
    active
) {

    const warning =
        document.getElementById(
            elementId
        );


    if (active === true) {

        warning.classList.add(
            "active"
        );

    } else {

        warning.classList.remove(
            "active"
        );
    }
}



// =====================================================
// UPDATE FIGHT STATUS
// =====================================================

function updateFightStatus(
    status
) {

    fightStatus.innerText =
        status;


    fightStatus.className =
        "fight-status";


    switch (status) {

        case "RUNNING":

            fightStatus.classList.add(
                "running"
            );

            break;


        case "PAUSED":

            fightStatus.classList.add(
                "paused"
            );

            break;


        case "FINISHED":

            fightStatus.classList.add(
                "finished"
            );

            break;


        default:

            fightStatus.classList.add(
                "ready"
            );

            break;
    }
}



// =====================================================
// SHOW WINNER SCREEN
// =====================================================

function showWinnerScreen(
    data
) {

    const winner =
        data.winner || "";


    const reason =
        data.reason || "";


    // ===============================================
    // DRAW
    // ===============================================

    if (
        winner.toLowerCase()
        ===
        "draw"
    ) {

        winnerText.innerText =
            "DRAW";


        winnerName.innerText =
            "";


    // ===============================================
    // AKA WINS
    // ===============================================

    } else if (
        winner === "AKA"
    ) {

        winnerText.innerText =
            "AKA WINS";


        winnerName.innerText =
            data.akaName || "AKA";


    // ===============================================
    // AO WINS
    // ===============================================

    } else if (
        winner === "AO"
    ) {

        winnerText.innerText =
            "AO WINS";


        winnerName.innerText =
            data.aoName || "AO";


    // ===============================================
    // FALLBACK
    // ===============================================

    } else {

        winnerText.innerText =
            winner;


        winnerName.innerText =
            "";
    }



    winnerReason.innerText =
        reason;



    finalScore.innerText =
        `${data.akaName || "AKA"} `
        +
        `${data.akaScore ?? 0}`
        +
        "  -  "
        +
        `${data.aoScore ?? 0} `
        +
        `${data.aoName || "AO"}`;



    winnerOverlay.classList.add(
        "show"
    );
}



// =====================================================
// HIDE WINNER SCREEN
// =====================================================

function hideWinnerScreen() {

    winnerOverlay.classList.remove(
        "show"
    );
}