import { useState } from "react";
import styles from "./button.module.css";

function Button({ name, onClick }) {
    const [hover, setHover] = useState(false);

    function handleMouseOver() {
        setHover(true);
    }

    function handleMouseOut() {
        setHover(false);
    }

    return (
        <button
            className={hover ? styles.buttonHover : styles.button}
            onClick={onClick}
            onMouseOver={handleMouseOver}
            onMouseOut={handleMouseOut}
        >
            {name}
        </button>
    );
}

export default Button;
