import NextAuth from "next-auth";
import GithubProvider from "next-auth/providers/github";
import {MongoDBAdapter} from "@next-auth/mongodb-adapter";
import {connectDB} from "@/util/database";

export const authOptions = {
  providers: [
    GithubProvider({
      clientId: '0d9a7e32fc5e403f37f3',
      clientSecret: '91d31781274844d89bb276b4825c1701774409f6',
    }),
  ],
  secret: 'trevi000',
  adapter: MongoDBAdapter(connectDB)
};
export default NextAuth(authOptions);
