import Header from "./components/Header";
import SearchSideBar from "./components/SearchSideBar";
import RestaurantCard from "./components/RestaurantCard";
import { GetServerSideProps, InferGetServerSidePropsType, Metadata } from "next";
import { Cuisine, Location, PRICE, PrismaClient } from "@prisma/client";

export const metadata: Metadata = {
  title: 'Search | OpenTable',
  description: 'Generated by create next app',
}

const prisma = new PrismaClient();

interface Restaurant {
  id: number
  name: string
  main_image: string
  price: PRICE
  cuisine: Cuisine
  location: Location
  slug: string
}

const fetchRestaurantsByCity = (city: string | undefined): Promise<Restaurant[]> => {
  const select = {
    id: true,
    name: true,
    main_image: true,
    price: true,
    cuisine: true,
    location: true,
    slug: true,
  }

  if (!city) return prisma.restaurant.findMany({
    select,
  });

  return prisma.restaurant.findMany({
    select,
    where: {
      location: {
        name: {
          contains: city,
          mode: 'insensitive',
        }
      }
    }
  });
}

export default async function Search({ searchParams }: { searchParams: { city: string | undefined } }) {
  const restaurants = await fetchRestaurantsByCity(searchParams.city);

  return (
    <>
      <Header />
      <div className="flex py-4 m-auto w-2/3 justify-between items-start">
        <SearchSideBar restaurants={restaurants} />
        <div className="w-5/6">
          {restaurants.length
            ? (restaurants.map((restaurant) => {
              return (
                <RestaurantCard restaurant={restaurant} />
              );
            }))
            : (<p>Sorry, we found no restaurant in this area.</p>)}

        </div>
      </div>
    </>
  )
}
