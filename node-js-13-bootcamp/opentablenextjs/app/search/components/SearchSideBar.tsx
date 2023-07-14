import { Cuisine, Location, PRICE } from "@prisma/client"

interface Restaurant {
  id: number
  cuisine: Cuisine
  location: Location
}

export default function SearchSideBar({ restaurants }: { restaurants: Restaurant[] }) {
  return (
    <div className="w-1/5">
      <div className="border-b pb-4">
        <h1 className="mb-2">Region</h1>
        {
          Array.from(new Set(restaurants.map((restaurant) => restaurant.location.name)))
            .map((locationName, index) => {
              return (
                <p className="font-light text-reg" key={index}>{locationName}</p>
              );
            })
        }
      </div>
      <div className="border-b pb-4 mt-3">
        <h1 className="mb-2">Cuisine</h1>
        {
          Array.from(new Set(restaurants.map((restaurant) => restaurant.cuisine.name)))
            .map((cuisineName, index) => {
              return (
                <p className="font-light text-reg" key={index}>{cuisineName}</p>
              );
            })
        }
      </div>
      <div className="mt-3 pb-4">
        <h1 className="mb-2">Price</h1>
        <div className="flex">
          <button className="border w-full text-reg font-light rounded-l p-2">
            $
          </button>
          <button
            className="border-r border-t border-b w-full text-reg font-light p-2"
          >
            $$
          </button>
          <button
            className="border-r border-t border-b w-full text-reg font-light p-2 rounded-r"
          >
            $$$
          </button>
        </div>
      </div>
    </div>
  );
}
