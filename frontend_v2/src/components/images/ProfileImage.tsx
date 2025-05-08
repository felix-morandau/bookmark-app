import "./profileimage.model.css"

function ProfileImage(props) {
    return (
        <div className="profile-container">
            <img src={props.src} alt="Profile Image" className="profile-image"/>
        </div>
    )
}
export default ProfileImage