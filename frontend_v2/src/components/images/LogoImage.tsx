import logo from '../../assets/bookmark_img.jpg';

function LogoImage() {
    return (
        <div className="login-container">
            <div className="logo-container">
                <img className="logo-image" src={logo} alt="Bookmark Logo" />
            </div>
            <div  className="login-form-container">

            </div>
        </div>
    )
}

export default LogoImage