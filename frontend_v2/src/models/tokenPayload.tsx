interface TokenPayload {
    iss: string;
    iat: number;
    exp: number;
    username: string;
    role: string;
}

export default TokenPayload;